package com.finflow.application.usecase.quote;

import com.finflow.application.dto.quote.ApproveQuoteRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.event.OrderConfirmed;
import com.finflow.domain.model.order.Order;
import com.finflow.domain.model.payable.PayableFactory;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.receivable.ReceivableFactory;
import com.finflow.domain.repository.OrderRepository;
import com.finflow.domain.repository.PayableRepository;
import com.finflow.domain.repository.QuoteRepository;
import com.finflow.domain.repository.ReceivableRepository;
import com.finflow.infrastructure.numbering.NumberGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ADR-0003: transição mais carregada do sistema. Guarda de estado (idempotência
 * por construção) + lock pessimista (fecha corrida entre requisições
 * concorrentes) + constraint {@code orders.quote_id UNIQUE} no banco como
 * última linha de defesa contra duplicidade.
 */
@Service
public class ApproveQuoteUseCase {

    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;
    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;
    private final NumberGenerator numberGenerator;
    private final ExchangeRateService exchangeRateService;
    private final ApplicationEventPublisher events;

    public ApproveQuoteUseCase(QuoteRepository quoteRepository, OrderRepository orderRepository,
                                ReceivableRepository receivableRepository, PayableRepository payableRepository,
                                NumberGenerator numberGenerator, ExchangeRateService exchangeRateService,
                                ApplicationEventPublisher events) {
        this.quoteRepository = quoteRepository;
        this.orderRepository = orderRepository;
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
        this.numberGenerator = numberGenerator;
        this.exchangeRateService = exchangeRateService;
        this.events = events;
    }

    @Transactional
    public Order execute(UUID quoteId, ApproveQuoteRequest request) {
        Quote quote = quoteRepository.findByIdForUpdate(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento", quoteId));

        if (quote.isApproved()) {
            // idempotente: segunda chamada (duplo clique/retry) retorna a Order já existente, não erro.
            return orderRepository.findByQuoteId(quoteId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Orçamento %s está APPROVED mas não tem Order associada -- estado inconsistente"
                                    .formatted(quoteId)));
        }
        quote.requireApprovable();

        LocalDate today = LocalDate.now();
        Order order = Order.fromQuote(numberGenerator.nextOrderNumber(), quote,
                request.paymentMethod(), request.installments(),
                currency -> exchangeRateService.rateFor(currency, today).getRate());
        order = orderRepository.save(order);

        receivableRepository.saveAll(ReceivableFactory.generate(order));
        payableRepository.saveAll(PayableFactory.generateBySupplier(order));

        quote.markApproved();

        events.publishEvent(new OrderConfirmed(order.getId(), order.getCustomer().getId(), order.getTotalSale()));

        return order;
    }
}

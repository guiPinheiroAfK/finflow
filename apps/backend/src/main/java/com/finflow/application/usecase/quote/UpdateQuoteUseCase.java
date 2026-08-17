package com.finflow.application.usecase.quote;

import com.finflow.application.dto.quote.QuoteItemRequest;
import com.finflow.application.dto.quote.QuoteRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.repository.ProductRepository;
import com.finflow.domain.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateQuoteUseCase {

    private final QuoteRepository quoteRepository;
    private final ProductRepository productRepository;

    public UpdateQuoteUseCase(QuoteRepository quoteRepository, ProductRepository productRepository) {
        this.quoteRepository = quoteRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Quote execute(UUID id, QuoteRequest request) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento", id));

        quote.updateHeader(request.validUntil(), request.notes());
        quote.clearItems();

        for (QuoteItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto", itemRequest.productId()));
            quote.addItem(product, itemRequest.description(), itemRequest.quantity(),
                    itemRequest.unitCost(), itemRequest.unitSale(), itemRequest.travelDate(),
                    itemRequest.passengerNames());
        }

        return quote;
    }
}

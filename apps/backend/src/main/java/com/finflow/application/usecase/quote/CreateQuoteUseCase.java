package com.finflow.application.usecase.quote;

import com.finflow.application.dto.quote.QuoteItemRequest;
import com.finflow.application.dto.quote.QuoteRequest;
import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.customer.Customer;
import com.finflow.domain.model.product.Product;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.user.User;
import com.finflow.domain.repository.CustomerRepository;
import com.finflow.domain.repository.ProductRepository;
import com.finflow.domain.repository.QuoteRepository;
import com.finflow.domain.repository.UserRepository;
import com.finflow.infrastructure.numbering.NumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateQuoteUseCase {

    private final QuoteRepository quoteRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final NumberGenerator numberGenerator;

    public CreateQuoteUseCase(QuoteRepository quoteRepository, CustomerRepository customerRepository,
                               UserRepository userRepository, ProductRepository productRepository,
                               NumberGenerator numberGenerator) {
        this.quoteRepository = quoteRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.numberGenerator = numberGenerator;
    }

    @Transactional
    public Quote execute(UUID sellerId, QuoteRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.customerId()));
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor", sellerId));

        Quote quote = Quote.create(numberGenerator.nextQuoteNumber(), customer, seller,
                request.validUntil(), request.notes());

        for (QuoteItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto", itemRequest.productId()));
            quote.addItem(product, itemRequest.description(), itemRequest.quantity(),
                    itemRequest.unitCost(), itemRequest.unitSale(), itemRequest.travelDate(),
                    itemRequest.passengerNames());
        }

        return quoteRepository.save(quote);
    }
}

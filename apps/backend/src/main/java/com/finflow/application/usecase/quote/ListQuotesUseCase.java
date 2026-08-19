package com.finflow.application.usecase.quote;

import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.model.quote.QuoteStatus;
import com.finflow.domain.repository.QuoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ListQuotesUseCase {

    private final QuoteRepository quoteRepository;

    public ListQuotesUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Transactional(readOnly = true)
    public Page<Quote> execute(QuoteStatus status, UUID customerId, Pageable pageable) {
        if (status != null) {
            return quoteRepository.findByStatus(status, pageable);
        }
        if (customerId != null) {
            return quoteRepository.findByCustomerId(customerId, pageable);
        }
        return quoteRepository.findAll(pageable);
    }
}

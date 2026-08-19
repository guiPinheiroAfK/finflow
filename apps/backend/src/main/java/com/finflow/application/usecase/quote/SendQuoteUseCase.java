package com.finflow.application.usecase.quote;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.quote.Quote;
import com.finflow.domain.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SendQuoteUseCase {

    private final QuoteRepository quoteRepository;

    public SendQuoteUseCase(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Transactional
    public Quote execute(UUID id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento", id));
        quote.markSent();
        return quote;
    }
}

package com.finflow.web.controller;

import com.finflow.application.dto.order.OrderResponse;
import com.finflow.application.dto.quote.ApproveQuoteRequest;
import com.finflow.application.dto.quote.QuoteRequest;
import com.finflow.application.dto.quote.QuoteResponse;
import com.finflow.application.mapper.OrderMapper;
import com.finflow.application.mapper.QuoteMapper;
import com.finflow.application.usecase.quote.ApproveQuoteUseCase;
import com.finflow.application.usecase.quote.CreateQuoteUseCase;
import com.finflow.application.usecase.quote.GetQuoteUseCase;
import com.finflow.application.usecase.quote.ListQuotesUseCase;
import com.finflow.application.usecase.quote.SendQuoteUseCase;
import com.finflow.application.usecase.quote.UpdateQuoteUseCase;
import com.finflow.domain.model.quote.QuoteStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {

    private final CreateQuoteUseCase createQuoteUseCase;
    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final GetQuoteUseCase getQuoteUseCase;
    private final ListQuotesUseCase listQuotesUseCase;
    private final SendQuoteUseCase sendQuoteUseCase;
    private final ApproveQuoteUseCase approveQuoteUseCase;
    private final QuoteMapper quoteMapper;
    private final OrderMapper orderMapper;

    public QuoteController(CreateQuoteUseCase createQuoteUseCase, UpdateQuoteUseCase updateQuoteUseCase,
                            GetQuoteUseCase getQuoteUseCase, ListQuotesUseCase listQuotesUseCase,
                            SendQuoteUseCase sendQuoteUseCase, ApproveQuoteUseCase approveQuoteUseCase,
                            QuoteMapper quoteMapper, OrderMapper orderMapper) {
        this.createQuoteUseCase = createQuoteUseCase;
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.getQuoteUseCase = getQuoteUseCase;
        this.listQuotesUseCase = listQuotesUseCase;
        this.sendQuoteUseCase = sendQuoteUseCase;
        this.approveQuoteUseCase = approveQuoteUseCase;
        this.quoteMapper = quoteMapper;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<QuoteResponse> create(Authentication authentication, @Valid @RequestBody QuoteRequest request) {
        UUID sellerId = UUID.fromString(authentication.getName());
        var quote = createQuoteUseCase.execute(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteMapper.toResponse(quote));
    }

    @GetMapping
    public ResponseEntity<Page<QuoteResponse>> list(@RequestParam(required = false) QuoteStatus status,
                                                      @RequestParam(required = false) UUID customer,
                                                      Pageable pageable) {
        return ResponseEntity.ok(listQuotesUseCase.execute(status, customer, pageable).map(quoteMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteMapper.toResponse(getQuoteUseCase.execute(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteResponse> update(@PathVariable UUID id, @Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteMapper.toResponse(updateQuoteUseCase.execute(id, request)));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<QuoteResponse> send(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteMapper.toResponse(sendQuoteUseCase.execute(id)));
    }

    // ADR-0003 §2: sempre 200 -- inclusive numa segunda chamada idempotente que
    // não cria Order nova, só devolve a existente.
    @PostMapping("/{id}/approve")
    public ResponseEntity<OrderResponse> approve(@PathVariable UUID id, @Valid @RequestBody ApproveQuoteRequest request) {
        var order = approveQuoteUseCase.execute(id, request);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}

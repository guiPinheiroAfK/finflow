package com.finflow.web.controller;

import com.finflow.application.dto.receivable.PayReceivableRequest;
import com.finflow.application.dto.receivable.ReceivableResponse;
import com.finflow.application.mapper.ReceivableMapper;
import com.finflow.application.usecase.receivable.GetReceivableUseCase;
import com.finflow.application.usecase.receivable.ListReceivablesUseCase;
import com.finflow.application.usecase.receivable.PayReceivableUseCase;
import com.finflow.domain.model.receivable.ReceivableStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/receivables")
public class ReceivableController {

    private final ListReceivablesUseCase listReceivablesUseCase;
    private final GetReceivableUseCase getReceivableUseCase;
    private final PayReceivableUseCase payReceivableUseCase;
    private final ReceivableMapper mapper;

    public ReceivableController(ListReceivablesUseCase listReceivablesUseCase, GetReceivableUseCase getReceivableUseCase,
                                 PayReceivableUseCase payReceivableUseCase, ReceivableMapper mapper) {
        this.listReceivablesUseCase = listReceivablesUseCase;
        this.getReceivableUseCase = getReceivableUseCase;
        this.payReceivableUseCase = payReceivableUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<Page<ReceivableResponse>> list(@RequestParam(required = false) ReceivableStatus status,
                                                           Pageable pageable) {
        return ResponseEntity.ok(listReceivablesUseCase.execute(status, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceivableResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getReceivableUseCase.execute(id)));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ReceivableResponse> pay(@PathVariable UUID id, @Valid @RequestBody PayReceivableRequest request) {
        return ResponseEntity.ok(mapper.toResponse(payReceivableUseCase.execute(id, request.amount())));
    }
}

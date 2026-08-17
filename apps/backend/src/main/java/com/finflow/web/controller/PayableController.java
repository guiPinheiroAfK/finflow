package com.finflow.web.controller;

import com.finflow.application.dto.payable.PayableResponse;
import com.finflow.application.mapper.PayableMapper;
import com.finflow.application.usecase.payable.GetPayableUseCase;
import com.finflow.application.usecase.payable.ListPayablesUseCase;
import com.finflow.application.usecase.payable.PayPayableUseCase;
import com.finflow.domain.model.payable.PayableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payables")
public class PayableController {

    private final ListPayablesUseCase listPayablesUseCase;
    private final GetPayableUseCase getPayableUseCase;
    private final PayPayableUseCase payPayableUseCase;
    private final PayableMapper mapper;

    public PayableController(ListPayablesUseCase listPayablesUseCase, GetPayableUseCase getPayableUseCase,
                              PayPayableUseCase payPayableUseCase, PayableMapper mapper) {
        this.listPayablesUseCase = listPayablesUseCase;
        this.getPayableUseCase = getPayableUseCase;
        this.payPayableUseCase = payPayableUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<Page<PayableResponse>> list(@RequestParam(required = false) PayableStatus status,
                                                        Pageable pageable) {
        return ResponseEntity.ok(listPayablesUseCase.execute(status, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayableResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getPayableUseCase.execute(id)));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PayableResponse> pay(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(payPayableUseCase.execute(id)));
    }
}

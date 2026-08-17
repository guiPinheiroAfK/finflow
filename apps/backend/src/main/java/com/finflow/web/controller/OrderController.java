package com.finflow.web.controller;

import com.finflow.application.dto.order.OrderResponse;
import com.finflow.application.mapper.OrderMapper;
import com.finflow.application.usecase.order.CancelOrderUseCase;
import com.finflow.application.usecase.order.GetOrderUseCase;
import com.finflow.application.usecase.order.IssueOrderUseCase;
import com.finflow.application.usecase.order.ListOrdersUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final IssueOrderUseCase issueOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderMapper mapper;

    public OrderController(GetOrderUseCase getOrderUseCase, ListOrdersUseCase listOrdersUseCase,
                            IssueOrderUseCase issueOrderUseCase, CancelOrderUseCase cancelOrderUseCase,
                            OrderMapper mapper) {
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.issueOrderUseCase = issueOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(listOrdersUseCase.execute(pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(getOrderUseCase.execute(id)));
    }

    @PostMapping("/{id}/issue")
    public ResponseEntity<OrderResponse> issue(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(issueOrderUseCase.execute(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(cancelOrderUseCase.execute(id)));
    }
}

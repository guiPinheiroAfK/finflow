package com.finflow.application.usecase.order;

import com.finflow.application.exception.ResourceNotFoundException;
import com.finflow.domain.model.order.Order;
import com.finflow.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IssueOrderUseCase {

    private final OrderRepository orderRepository;

    public IssueOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order execute(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", id));
        order.issue();
        return order;
    }
}

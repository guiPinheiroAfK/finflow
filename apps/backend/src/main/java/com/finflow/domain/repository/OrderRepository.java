package com.finflow.domain.repository;

import com.finflow.domain.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByQuoteId(UUID quoteId);
}

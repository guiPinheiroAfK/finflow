package com.finflow.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

/** ADR-0002/0003: publicado dentro da transação de aprovação; materializado na outbox BEFORE_COMMIT. */
public record OrderConfirmed(UUID orderId, UUID customerId, BigDecimal totalSale) implements DomainEvent {

    @Override
    public String aggregateType() {
        return "Order";
    }

    @Override
    public UUID aggregateId() {
        return orderId;
    }

    @Override
    public String eventType() {
        return "sales.order.confirmed";
    }
}

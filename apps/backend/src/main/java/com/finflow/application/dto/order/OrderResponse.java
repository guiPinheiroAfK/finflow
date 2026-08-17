package com.finflow.application.dto.order;

import com.finflow.domain.model.order.OrderStatus;
import com.finflow.domain.model.shared.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderNumber,
        UUID quoteId,
        UUID customerId,
        String customerName,
        UUID sellerId,
        String sellerName,
        OrderStatus status,
        PaymentMethod paymentMethod,
        int installments,
        BigDecimal totalSale,
        BigDecimal totalCost,
        BigDecimal grossMargin,
        BigDecimal commissionPct,
        BigDecimal commissionValue,
        LocalDateTime confirmedAt,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {
}

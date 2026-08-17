package com.finflow.application.dto.receivable;

import com.finflow.domain.model.receivable.ReceivableStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceivableResponse(
        UUID id,
        UUID orderId,
        String orderNumber,
        UUID customerId,
        String customerName,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDateTime paidAt,
        BigDecimal paidAmount,
        ReceivableStatus status,
        LocalDateTime createdAt
) {
}

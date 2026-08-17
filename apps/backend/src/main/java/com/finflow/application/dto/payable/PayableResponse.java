package com.finflow.application.dto.payable;

import com.finflow.domain.model.payable.PayableStatus;
import com.finflow.domain.model.shared.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PayableResponse(
        UUID id,
        UUID orderId,
        String orderNumber,
        UUID supplierId,
        String supplierName,
        String description,
        BigDecimal amount,
        Currency currency,
        BigDecimal exchangeRate,
        BigDecimal amountBrl,
        LocalDate dueDate,
        LocalDateTime paidAt,
        PayableStatus status,
        LocalDateTime createdAt
) {
}

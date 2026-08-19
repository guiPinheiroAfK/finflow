package com.finflow.application.dto.quote;

import com.finflow.domain.model.quote.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String quoteNumber,
        UUID customerId,
        String customerName,
        UUID sellerId,
        String sellerName,
        QuoteStatus status,
        LocalDate validUntil,
        String notes,
        BigDecimal totalCost,
        BigDecimal totalSale,
        BigDecimal margin,
        List<QuoteItemResponse> items,
        LocalDateTime createdAt
) {
}

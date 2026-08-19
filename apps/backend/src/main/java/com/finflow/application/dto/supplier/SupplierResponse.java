package com.finflow.application.dto.supplier;

import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.supplier.SupplierCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        SupplierCategory category,
        String document,
        String contactName,
        String email,
        int paymentTermDays,
        Currency currency,
        LocalDateTime createdAt
) {
}

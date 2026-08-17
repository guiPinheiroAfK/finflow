package com.finflow.application.dto.product;

import com.finflow.domain.model.product.ProductCategory;
import com.finflow.domain.model.shared.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        ProductCategory category,
        UUID supplierId,
        String supplierName,
        BigDecimal costPrice,
        Currency currency,
        BigDecimal salePrice,
        BigDecimal markupPct,
        boolean active,
        LocalDateTime createdAt
) {
}

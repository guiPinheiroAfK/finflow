package com.finflow.application.dto.product;

import com.finflow.domain.model.product.ProductCategory;
import com.finflow.domain.model.shared.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank String name,
        @NotNull ProductCategory category,
        @NotNull UUID supplierId,
        @NotNull @DecimalMin(value = "0") BigDecimal costPrice,
        @NotNull Currency currency,
        @NotNull @DecimalMin(value = "0") BigDecimal salePrice
) {
}

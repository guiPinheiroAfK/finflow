package com.finflow.application.dto.supplier;

import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.model.supplier.SupplierCategory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SupplierRequest(
        @NotBlank String name,
        @NotNull SupplierCategory category,
        String document,
        String contactName,
        @Email String email,
        @Min(0) int paymentTermDays,
        @NotNull Currency currency
) {
}

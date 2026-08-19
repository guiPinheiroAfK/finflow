package com.finflow.application.dto.quote;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteItemRequest(
        @NotNull UUID productId,
        String description,
        @Min(1) int quantity,
        @NotNull @DecimalMin("0") BigDecimal unitCost,
        @NotNull @DecimalMin("0") BigDecimal unitSale,
        LocalDate travelDate,
        List<String> passengerNames
) {
}

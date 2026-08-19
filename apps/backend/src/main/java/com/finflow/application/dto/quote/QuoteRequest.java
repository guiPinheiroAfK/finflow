package com.finflow.application.dto.quote;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteRequest(
        @NotNull UUID customerId,
        LocalDate validUntil,
        String notes,
        @NotEmpty @Valid List<QuoteItemRequest> items
) {
}

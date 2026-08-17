package com.finflow.application.dto.quote;

import com.finflow.domain.model.shared.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ApproveQuoteRequest(
        @NotNull PaymentMethod paymentMethod,
        @Min(1) int installments
) {
}

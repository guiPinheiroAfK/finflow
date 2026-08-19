package com.finflow.application.dto.order;

import com.finflow.domain.model.shared.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String description,
        int quantity,
        BigDecimal unitCost,
        Currency unitCostCurrency,
        BigDecimal unitCostExchangeRate,
        BigDecimal unitCostBrl,
        BigDecimal unitSale,
        LocalDate travelDate,
        List<String> passengerNames
) {
}

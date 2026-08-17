package com.finflow.application.dto.quote;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuoteItemResponse(
        UUID id,
        UUID productId,
        String productName,
        String description,
        int quantity,
        BigDecimal unitCost,
        BigDecimal unitSale,
        LocalDate travelDate,
        List<String> passengerNames
) {
}

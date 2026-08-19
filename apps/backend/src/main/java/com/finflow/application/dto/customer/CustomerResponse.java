package com.finflow.application.dto.customer;

import com.finflow.domain.model.customer.CustomerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        CustomerType type,
        String name,
        String document,
        String email,
        String phone,
        AddressDto address,
        List<String> tags,
        LocalDateTime createdAt
) {
}

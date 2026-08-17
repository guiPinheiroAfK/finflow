package com.finflow.application.dto.customer;

import com.finflow.domain.model.customer.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerRequest(
        @NotNull CustomerType type,
        @NotBlank String name,
        @NotBlank String document,
        @Email String email,
        String phone,
        AddressDto address,
        List<String> tags
) {
}

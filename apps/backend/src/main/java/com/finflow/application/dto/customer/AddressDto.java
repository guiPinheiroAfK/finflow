package com.finflow.application.dto.customer;

public record AddressDto(
        String street,
        String number,
        String city,
        String state,
        String zip
) {
}

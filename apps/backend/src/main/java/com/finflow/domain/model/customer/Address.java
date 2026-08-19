package com.finflow.domain.model.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Address(
        @Column(name = "address_street") String street,
        @Column(name = "address_number") String number,
        @Column(name = "address_city") String city,
        @Column(name = "address_state") String state,
        @Column(name = "address_zip") String zip
) {
}

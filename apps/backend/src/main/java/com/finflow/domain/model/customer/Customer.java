package com.finflow.domain.model.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String document;

    private String email;

    private String phone;

    @Embedded
    private Address address;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> tags;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Customer create(CustomerType type, String name, String document,
                                   String email, String phone, Address address, List<String> tags) {
        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.type = type;
        customer.name = name;
        customer.document = document;
        customer.email = email;
        customer.phone = phone;
        customer.address = address;
        customer.tags = tags;
        customer.createdAt = LocalDateTime.now();
        return customer;
    }

    public void update(String name, String email, String phone, Address address, List<String> tags) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.tags = tags;
    }
}

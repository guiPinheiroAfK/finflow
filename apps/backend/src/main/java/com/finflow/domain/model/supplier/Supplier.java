package com.finflow.domain.model.supplier;

import com.finflow.domain.model.shared.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supplier {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierCategory category;

    private String document;

    @Column(name = "contact_name")
    private String contactName;

    private String email;

    @Column(name = "payment_term_days", nullable = false)
    private int paymentTermDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static Supplier create(String name, SupplierCategory category, String document,
                                   String contactName, String email, int paymentTermDays, Currency currency) {
        Supplier supplier = new Supplier();
        supplier.id = UUID.randomUUID();
        supplier.name = name;
        supplier.category = category;
        supplier.document = document;
        supplier.contactName = contactName;
        supplier.email = email;
        supplier.paymentTermDays = paymentTermDays;
        supplier.currency = currency;
        supplier.createdAt = LocalDateTime.now();
        return supplier;
    }

    public void update(String name, SupplierCategory category, String contactName,
                        String email, int paymentTermDays, Currency currency) {
        this.name = name;
        this.category = category;
        this.contactName = contactName;
        this.email = email;
        this.paymentTermDays = paymentTermDays;
        this.currency = currency;
    }
}

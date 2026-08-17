package com.finflow.domain.model.bankaccount;

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
@Table(name = "bank_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankAccount {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    private String agency;

    @Column(name = "account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static BankAccount create(String name, String bankName, String agency,
                                      String accountNumber, Currency currency) {
        BankAccount account = new BankAccount();
        account.id = UUID.randomUUID();
        account.name = name;
        account.bankName = bankName;
        account.agency = agency;
        account.accountNumber = accountNumber;
        account.currency = currency;
        account.active = true;
        account.createdAt = LocalDateTime.now();
        return account;
    }
}

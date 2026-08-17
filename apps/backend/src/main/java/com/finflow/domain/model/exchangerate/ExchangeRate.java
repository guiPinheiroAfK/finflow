package com.finflow.domain.model.exchangerate;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Cotação do dia -> BRL. Nunca usada para recalcular transações passadas (ADR-0001 §4). */
@Entity
@Table(name = "exchange_rates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal rate;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static ExchangeRate of(Currency currency, BigDecimal rate, LocalDate rateDate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.id = UUID.randomUUID();
        exchangeRate.currency = currency;
        exchangeRate.rate = rate;
        exchangeRate.rateDate = rateDate;
        exchangeRate.createdAt = LocalDateTime.now();
        return exchangeRate;
    }
}

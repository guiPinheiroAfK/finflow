package com.finflow.domain.model.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * ADR-0001: dinheiro nunca trafega como BigDecimal solto -- anda sempre
 * acompanhado da moeda. Escala normalizada para 2 casas, HALF_UP (bate com o
 * arredondamento bancário, requisito da conciliação -- ADR-0004).
 */
@Embeddable
public record Money(
        @Column(precision = 19, scale = 2) BigDecimal amount,
        @Enumerated(EnumType.STRING) Currency currency
) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public static Money of(String amount, Currency currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    /** Converte para BRL usando uma taxa já obtida -- nunca recalcular câmbio histórico (ADR-0001 §4). */
    public Money convertToBrl(BigDecimal exchangeRate) {
        if (currency == Currency.BRL) {
            return this;
        }
        return new Money(amount.multiply(exchangeRate), Currency.BRL);
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    private void requireSameCurrency(Money other) {
        if (currency != other.currency) {
            throw new IllegalArgumentException(
                    "Operação entre moedas diferentes: %s vs %s".formatted(currency, other.currency));
        }
    }
}

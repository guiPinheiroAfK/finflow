package com.finflow.domain.repository;

import com.finflow.domain.model.exchangerate.ExchangeRate;
import com.finflow.domain.model.shared.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    Optional<ExchangeRate> findTopByCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
            Currency currency, LocalDate date);
}

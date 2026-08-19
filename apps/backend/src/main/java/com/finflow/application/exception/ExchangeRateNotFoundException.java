package com.finflow.application.exception;

import com.finflow.domain.model.shared.Currency;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class ExchangeRateNotFoundException extends BusinessException {
    public ExchangeRateNotFoundException(Currency currency, LocalDate date) {
        super("Nenhuma cotação de %s encontrada em ou antes de %s".formatted(currency, date),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

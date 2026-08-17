package com.finflow.application.usecase.quote;

import com.finflow.application.exception.ExchangeRateNotFoundException;
import com.finflow.domain.model.exchangerate.ExchangeRate;
import com.finflow.domain.model.shared.Currency;
import com.finflow.domain.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/** ADR-0001 §4: obtém a cotação do dia para congelar no snapshot da Order -- nunca recalculada depois. */
@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeRate rateFor(Currency currency, LocalDate date) {
        return exchangeRateRepository
                .findTopByCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(currency, date)
                .orElseThrow(() -> new ExchangeRateNotFoundException(currency, date));
    }
}

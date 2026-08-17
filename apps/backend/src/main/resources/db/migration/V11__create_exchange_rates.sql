CREATE TABLE exchange_rates (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    currency    VARCHAR(3)   NOT NULL CHECK (currency IN ('USD', 'EUR', 'ARS')),  -- sempre cotação -> BRL
    rate        NUMERIC(19,6) NOT NULL,
    rate_date   DATE         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    -- fonte da cotação do dia; NÃO usada para recalcular transações passadas (ADR-0001 §4)
    UNIQUE (currency, rate_date)
);

CREATE INDEX idx_exchange_rates_currency_date ON exchange_rates (currency, rate_date DESC);

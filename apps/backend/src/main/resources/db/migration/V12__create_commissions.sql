CREATE TABLE commissions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID         NOT NULL UNIQUE REFERENCES orders (id),  -- uma comissão por venda
    seller_id   UUID         NOT NULL REFERENCES users (id),
    pct         NUMERIC(9,4) NOT NULL,
    amount      NUMERIC(19,2) NOT NULL,
    month       INTEGER      NOT NULL CHECK (month BETWEEN 1 AND 12),
    year        INTEGER      NOT NULL,
    paid        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_commissions_seller_period ON commissions (seller_id, year, month);

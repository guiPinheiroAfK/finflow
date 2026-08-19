CREATE TABLE orders (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number      VARCHAR(32)  NOT NULL UNIQUE,   -- VND-2024-001
    quote_id          UUID         UNIQUE REFERENCES quotes (id),  -- ADR-0003: NULL = venda direta; UNIQUE = idempotência da aprovação
    customer_id       UUID         NOT NULL REFERENCES customers (id),
    seller_id         UUID         NOT NULL REFERENCES users (id),
    status            VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMED'
                      CHECK (status IN ('CONFIRMED', 'ISSUED', 'CANCELLED', 'COMPLETED')),
    payment_method    VARCHAR(20)  NOT NULL
                      CHECK (payment_method IN ('DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'BOLETO', 'TRANSFERENCIA')),
    installments      INTEGER      NOT NULL DEFAULT 1 CHECK (installments > 0),
    total_sale        NUMERIC(19,2) NOT NULL,
    total_cost        NUMERIC(19,2) NOT NULL,
    gross_margin      NUMERIC(19,2) NOT NULL,
    commission_pct    NUMERIC(9,4) NOT NULL DEFAULT 0,
    commission_value  NUMERIC(19,2) NOT NULL DEFAULT 0,
    confirmed_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer ON orders (customer_id);
CREATE INDEX idx_orders_seller ON orders (seller_id);
CREATE INDEX idx_orders_status ON orders (status);

-- ADR-0003: snapshot imutável dos itens no momento da aprovação/venda.
-- ADR-0001 §4: se o custo do fornecedor for em moeda estrangeira, o trio
-- amount/currency + exchange_rate + amount_brl é congelado aqui.
CREATE TABLE order_items (
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id                  UUID         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id                UUID         NOT NULL REFERENCES products (id),
    description                VARCHAR(500),
    quantity                  INTEGER      NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_cost                 NUMERIC(19,2) NOT NULL,      -- valor na moeda original
    unit_cost_currency        VARCHAR(3)   NOT NULL DEFAULT 'BRL'
                               CHECK (unit_cost_currency IN ('BRL', 'USD', 'EUR', 'ARS')),
    unit_cost_exchange_rate   NUMERIC(19,6),                -- NULL quando moeda original já é BRL
    unit_cost_brl             NUMERIC(19,2) NOT NULL,       -- congelado; nunca recalculado
    unit_sale                 NUMERIC(19,2) NOT NULL,       -- venda é sempre BRL
    travel_date                DATE,
    passenger_names            TEXT[]       NOT NULL DEFAULT '{}'
);

CREATE INDEX idx_order_items_order ON order_items (order_id);

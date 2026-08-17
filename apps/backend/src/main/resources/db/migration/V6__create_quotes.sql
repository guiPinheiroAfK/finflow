CREATE TABLE quotes (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_number  VARCHAR(32)  NOT NULL UNIQUE,   -- ORC-2024-001
    customer_id   UUID         NOT NULL REFERENCES customers (id),
    seller_id     UUID         NOT NULL REFERENCES users (id),
    status        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                  CHECK (status IN ('DRAFT', 'SENT', 'APPROVED', 'REJECTED', 'EXPIRED')),
    valid_until   DATE,
    notes         TEXT,
    total_cost    NUMERIC(19,2) NOT NULL DEFAULT 0,   -- calculado a partir dos itens
    total_sale    NUMERIC(19,2) NOT NULL DEFAULT 0,
    margin        NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_quotes_customer ON quotes (customer_id);
CREATE INDEX idx_quotes_status ON quotes (status);

CREATE TABLE quote_items (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id          UUID         NOT NULL REFERENCES quotes (id) ON DELETE CASCADE,
    product_id        UUID         NOT NULL REFERENCES products (id),
    description       VARCHAR(500),
    quantity          INTEGER      NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_cost         NUMERIC(19,2) NOT NULL,
    unit_sale         NUMERIC(19,2) NOT NULL,
    travel_date       DATE,
    passenger_names   TEXT[]       NOT NULL DEFAULT '{}'
);

CREATE INDEX idx_quote_items_quote ON quote_items (quote_id);

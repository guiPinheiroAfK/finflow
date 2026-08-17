CREATE TABLE payables (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID         REFERENCES orders (id),   -- nullable: pagável avulso (não ligado a venda)
    supplier_id     UUID         NOT NULL REFERENCES suppliers (id),
    description     VARCHAR(500),
    amount          NUMERIC(19,2) NOT NULL,      -- valor na moeda original (ADR-0001 §4)
    currency        VARCHAR(3)   NOT NULL DEFAULT 'BRL'
                    CHECK (currency IN ('BRL', 'USD', 'EUR', 'ARS')),
    exchange_rate   NUMERIC(19,6),                -- NULL quando currency = BRL
    amount_brl      NUMERIC(19,2) NOT NULL,       -- congelado; nunca recalculado
    due_date        DATE         NOT NULL,
    paid_at         TIMESTAMPTZ,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),
    bank_account_id UUID         REFERENCES bank_accounts (id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_payables_order ON payables (order_id);
CREATE INDEX idx_payables_supplier ON payables (supplier_id);
CREATE INDEX idx_payables_status_due ON payables (status, due_date);

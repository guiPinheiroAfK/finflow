CREATE TABLE receivables (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID         NOT NULL REFERENCES orders (id),
    customer_id     UUID         NOT NULL REFERENCES customers (id),
    description     VARCHAR(500),
    amount          NUMERIC(19,2) NOT NULL,     -- parcela; soma das parcelas == orders.total_sale (ADR-0001 §5)
    due_date        DATE         NOT NULL,
    paid_at         TIMESTAMPTZ,
    paid_amount     NUMERIC(19,2),
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE', 'CANCELLED')),
    payment_method  VARCHAR(20)
                    CHECK (payment_method IN ('DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'BOLETO', 'TRANSFERENCIA')),
    bank_account_id UUID         REFERENCES bank_accounts (id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_receivables_order ON receivables (order_id);
CREATE INDEX idx_receivables_customer ON receivables (customer_id);
CREATE INDEX idx_receivables_status_due ON receivables (status, due_date);

CREATE TABLE bank_transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_account_id UUID         NOT NULL REFERENCES bank_accounts (id),
    date            DATE         NOT NULL,
    description     VARCHAR(500) NOT NULL,        -- texto livre do extrato (OFX/CSV)
    amount          NUMERIC(19,2) NOT NULL,        -- positivo = crédito, negativo = débito
    type            VARCHAR(10)  NOT NULL CHECK (type IN ('CREDIT', 'DEBIT')),
    reconciled      BOOLEAN      NOT NULL DEFAULT FALSE,
    receivable_id   UUID         REFERENCES receivables (id),
    payable_id      UUID         REFERENCES payables (id),
    -- ADR-0004: auditoria da decisão de matching (score/margem sempre gravados, mesmo em revisão manual)
    matched_by      VARCHAR(10)  CHECK (matched_by IN ('AUTO', 'MANUAL')),
    matched_score   NUMERIC(4,3),
    match_margin    NUMERIC(4,3),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_bank_tx_single_match
        CHECK (NOT (receivable_id IS NOT NULL AND payable_id IS NOT NULL))
);

CREATE INDEX idx_bank_tx_account_date ON bank_transactions (bank_account_id, date);
CREATE INDEX idx_bank_tx_unreconciled ON bank_transactions (reconciled) WHERE reconciled = FALSE;

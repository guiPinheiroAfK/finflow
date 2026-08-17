CREATE TABLE bank_accounts (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255) NOT NULL,
    bank_name      VARCHAR(255) NOT NULL,
    agency         VARCHAR(32),
    account_number VARCHAR(32),
    currency       VARCHAR(3)   NOT NULL DEFAULT 'BRL'
                   CHECK (currency IN ('BRL', 'USD', 'EUR', 'ARS')),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

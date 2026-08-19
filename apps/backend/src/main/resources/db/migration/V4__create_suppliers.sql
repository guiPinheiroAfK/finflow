CREATE TABLE suppliers (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name               VARCHAR(255) NOT NULL,
    category           VARCHAR(20)  NOT NULL
                       CHECK (category IN ('HOTEL', 'AEREA', 'TRANSFER', 'PASSEIO', 'SEGURO', 'OUTRO')),
    document           VARCHAR(20),
    contact_name       VARCHAR(255),
    email              VARCHAR(255),
    payment_term_days  INTEGER      NOT NULL DEFAULT 0,
    currency           VARCHAR(3)   NOT NULL DEFAULT 'BRL'
                       CHECK (currency IN ('BRL', 'USD', 'EUR', 'ARS')),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_suppliers_category ON suppliers (category);

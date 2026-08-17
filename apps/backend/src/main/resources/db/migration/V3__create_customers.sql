CREATE TABLE customers (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type         VARCHAR(20)  NOT NULL CHECK (type IN ('PESSOA_FISICA', 'PESSOA_JURIDICA')),
    name         VARCHAR(255) NOT NULL,
    document     VARCHAR(20)  NOT NULL UNIQUE,     -- CPF/CNPJ, dígitos apenas
    email        VARCHAR(255),
    phone        VARCHAR(32),
    address_street  VARCHAR(255),
    address_number  VARCHAR(20),
    address_city    VARCHAR(120),
    address_state   VARCHAR(2),
    address_zip     VARCHAR(10),
    tags         TEXT[]       NOT NULL DEFAULT '{}',  -- VIP, INADIMPLENTE, RECORRENTE
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_customers_document ON customers (document);
CREATE INDEX idx_customers_name ON customers (name);

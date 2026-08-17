CREATE TABLE products (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    category     VARCHAR(20)  NOT NULL
                 CHECK (category IN ('PACOTE', 'PASSAGEM', 'HOSPEDAGEM', 'TRANSFER', 'SEGURO', 'INGRESSO')),
    supplier_id  UUID         NOT NULL REFERENCES suppliers (id),
    cost_price   NUMERIC(19,2) NOT NULL,          -- ADR-0001 §2: escala 2
    currency     VARCHAR(3)   NOT NULL DEFAULT 'BRL'
                 CHECK (currency IN ('BRL', 'USD', 'EUR', 'ARS')),
    sale_price   NUMERIC(19,2) NOT NULL,
    -- markup_pct é derivado, nunca gravado por fora do cálculo -> coluna gerada
    markup_pct   NUMERIC(9,4) GENERATED ALWAYS AS (
                     CASE WHEN cost_price = 0 THEN NULL
                          ELSE round((sale_price - cost_price) / cost_price, 4)
                     END
                 ) STORED,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_supplier ON products (supplier_id);
CREATE INDEX idx_products_category ON products (category);

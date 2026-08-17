-- ADR-0002: Transactional Outbox. Escrita na mesma transação do estado de
-- negócio (BEFORE_COMMIT); um relay assíncrono publica no Kafka.
CREATE TABLE outbox (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(64)  NOT NULL,     -- "Order", "Receivable", ...
    aggregate_id   UUID         NOT NULL,     -- chave de partição Kafka (ordem por agregado)
    event_type     VARCHAR(128) NOT NULL,     -- topic, ex.: "sales.order.confirmed"
    payload        JSONB        NOT NULL,
    occurred_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published_at   TIMESTAMPTZ,               -- NULL = ainda não publicado
    attempts       INTEGER      NOT NULL DEFAULT 0
);

-- índice parcial: o relay só varre o que falta publicar (ADR-0002 §3-4)
CREATE INDEX idx_outbox_unpublished ON outbox (occurred_at) WHERE published_at IS NULL;

-- ADR-0002 §5: entrega at-least-once exige consumidores idempotentes.
-- Cada consumidor registra os event_id (outbox.id) já processados.
-- PK composta: o mesmo evento pode (e deve) ser processado por consumidores
-- lógicos distintos -- event_id sozinho bloquearia o segundo consumidor.
CREATE TABLE processed_events (
    event_id     UUID         NOT NULL,   -- outbox.id
    consumer     VARCHAR(128) NOT NULL,   -- nome lógico do consumidor
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    PRIMARY KEY (event_id, consumer)
);

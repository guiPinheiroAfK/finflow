# ADR-0002: Eventos de negócio via Outbox transacional + Kafka

- **Status:** Accepted
- **Data:** 2026-08-17
- **Contexto de domínio:** Cross-cutting (Comercial ↔ Financeiro)

## Contexto

Eventos de negócio (`sales.order.confirmed`, `financial.receivable.overdue`,
etc.) disparam reações: confirmar uma venda gera recebíveis/pagáveis, notifica,
alimenta relatórios. Queremos publicar esses eventos em Kafka.

O problema difícil é o **dual write**: a mudança no banco (venda confirmada) e a
publicação no Kafka são dois sistemas distintos, e não há transação distribuída
entre eles. Duas ordens ingênuas, ambas erradas:

- **Publicar dentro do `@Transactional`, antes do commit:** se a transação der
  rollback depois, o evento já foi para o Kafka → **evento fantasma** de uma
  venda que não existe.
- **Publicar depois do commit, no código da aplicação:** se o processo cair
  entre o commit e a publicação → **evento perdido**, e a venda existe sem que
  ninguém a jusante saiba.

Publicar Kafka "cru" dentro do service é o erro que denuncia desconhecimento do
problema. A decisão precisa garantir atomicidade entre *estado* e *intenção de
publicar*.

## Decisão

Adotamos o padrão **Transactional Outbox** com entrega **at-least-once**.

### 1. Fluxo

```
┌─ @Transactional (uma transação) ──────────────────────────┐
│  1. muda estado de negócio (Order -> CONFIRMED)           │
│  2. grava linha em outbox (mesmo commit)                  │
└───────────────────────────────────────────────────────────┘
                          │ commit atômico
                          ▼
   Relay (poller)  ── lê outbox não publicada ──▶ Kafka topic
                          │
                          └─ marca published_at
                          ▼
   Consumers (idempotentes) reagem
```

O estado e a linha da outbox commitam **juntos ou não commitam**. Um relay
assíncrono lê a outbox e publica. Se o relay cair, a linha continua lá e será
publicada na próxima passada → nada se perde. Se publicar duas vezes (crash
após publicar, antes de marcar) → consumidor idempotente absorve.

### 2. Domínio levanta evento sem conhecer a outbox

O service de negócio publica um evento de aplicação; um listener o materializa
na outbox **dentro da mesma transação** (`BEFORE_COMMIT`):

```java
// no use case, dentro do @Transactional
events.publishEvent(new OrderConfirmed(order.id(), order.customerId(),
                                       order.totalSale()));

// infraestrutura — roda na MESMA transação, garante atomicidade
@Component
class OutboxRelayWriter {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void on(OrderConfirmed e) {
        outboxRepository.save(OutboxEvent.of(
            "Order", e.orderId(), "sales.order.confirmed", toJson(e)));
    }
}
```

`BEFORE_COMMIT` é deliberado: a escrita na outbox precisa entrar no mesmo commit
do estado. O domínio (`domain/`) não importa Kafka nem outbox — só levanta o
evento. Acoplamento fica na `infrastructure/messaging`.

### 3. Tabela `outbox`

```sql
CREATE TABLE outbox (
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(64)  NOT NULL,   -- "Order", "Receivable"
    aggregate_id   UUID         NOT NULL,   -- chave de partição p/ ordenação
    event_type     VARCHAR(128) NOT NULL,   -- topic: "sales.order.confirmed"
    payload        JSONB        NOT NULL,
    occurred_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published_at   TIMESTAMPTZ,             -- NULL = ainda não publicado
    attempts       INT          NOT NULL DEFAULT 0
);
-- índice parcial: o relay só varre o que falta publicar
CREATE INDEX idx_outbox_unpublished ON outbox (occurred_at)
    WHERE published_at IS NULL;
```

### 4. Relay (polling publisher)

```java
@Scheduled(fixedDelay = 1000)
@Transactional
void relay() {
    // SKIP LOCKED permite escalar o relay horizontalmente sem publicar 2x
    var batch = outboxRepository.lockUnpublishedBatch(100); // FOR UPDATE SKIP LOCKED
    for (var ev : batch) {
        kafka.send(ev.eventType(), ev.aggregateId().toString(), ev.payload())
             .whenComplete((r, ex) -> { /* ver §6 */ });
        ev.markPublished();
    }
}
```

- **Chave de partição = `aggregate_id`.** Garante que eventos do mesmo agregado
  (mesma Order) caiam na mesma partição e mantenham ordem. Ordem global não é
  garantida nem necessária.
- `FOR UPDATE SKIP LOCKED` deixa o padrão pronto para múltiplas instâncias.

> **Alternativa de produção documentada:** trocar o poller por **Debezium**
> (CDC lendo o WAL do Postgres via Kafka Connect) elimina o polling e a latência.
> Mantemos o poller como baseline por ser in-app, testável com Testcontainers e
> suficiente para o volume do finflow. A migração para Debezium não muda o
> schema da outbox — só o mecanismo de leitura.

### 5. Consumidores idempotentes (at-least-once ⇒ obrigatório)

Entrega é *at-least-once*: todo consumidor **deve** tolerar duplicatas. O `id`
da outbox viaja no header do registro Kafka como chave de deduplicação. Cada
consumidor registra os `event_id` já processados (tabela
`processed_events(event_id PK, processed_at)`) e ignora repetição, ou usa
idempotência natural (ex.: `UPSERT` por chave de negócio).

### 6. Retry e poison messages

- Falha de publicação: a linha continua com `published_at IS NULL`; a próxima
  passada tenta de novo, incrementando `attempts`.
- `attempts >= 10`: linha marcada como *dead* (não mais varrida) e um alerta é
  emitido — evita loop infinito de mensagem venenosa. Inspeção manual.

### 7. Tópicos (contrato de eventos)

Mantém-se o catálogo do spec. Todo payload carrega envelope comum
`{ eventId, occurredAt, aggregateId, ... }`:

| Topic | Chave (partição) | Payload |
|-------|------------------|---------|
| `sales.order.confirmed` | orderId | `{ orderId, customerId, totalSale }` |
| `sales.order.cancelled` | orderId | `{ orderId, reason }` |
| `financial.receivable.overdue` | receivableId | `{ receivableId, daysOverdue }` |
| `financial.payable.due-today` | payableId | `{ payableId, supplierId, amount }` |
| `financial.reconciliation.completed` | bankAccountId | `{ bankAccountId, matchedCount, pendingCount }` |

## Alternativas consideradas

- **Publicar Kafka direto no `@Transactional`.** Simples, mas sofre dual write
  (evento fantasma em rollback / evento perdido em crash). Descartado — é
  justamente o antipadrão que este ADR existe para evitar.
- **Só Spring `ApplicationEvent`, sem Kafka.** Resolveria as reações in-process
  do monólito com menos infra. Correto para um produto real, mas **contraria o
  objetivo de portfólio** (demonstrar Kafka aplicado com competência). Mantemos
  Kafka *e* o fazemos certo, via outbox.
- **Spring Modulith Event Publication Registry.** É essencialmente uma outbox
  pronta. Ótimo, mas esconde o mecanismo que queremos demonstrar
  explicitamente. Fica como evolução possível.

## Consequências

- **Positivas:** zero perda/fantasma de eventos; atomicidade estado↔publicação
  sem transação distribuída; ordem por agregado; relay escalável; caminho de
  upgrade para Debezium sem mexer no schema. Demonstra domínio do problema de
  dual write — o diferencial sênior do repo.
- **Negativas / custos:** latência de até ~1s (intervalo do poller); consumidores
  obrigados a ser idempotentes; uma tabela e um scheduler a mais; `attempts`/DLQ
  a monitorar.
- **Impacto em schema/código:**
  - Flyway **V13** `outbox` (aditivo, sem FK, não renumera V1–V12) e
    **V14** `processed_events`.
  - `infrastructure/messaging`: `OutboxEvent` (entity), `OutboxRepository`
    (query `lockUnpublishedBatch` com `SKIP LOCKED`), `OutboxRelay`
    (`@Scheduled`), `OutboxRelayWriter` (`@TransactionalEventListener`).
  - `domain/`: eventos como records puros (`OrderConfirmed`, ...), levantados via
    `ApplicationEventPublisher`. Domínio permanece sem dependência de Kafka.
  - Habilitar `@EnableScheduling`.
  - Teste de integração com Testcontainers (Postgres + Kafka) cobrindo o caminho
    commit → outbox → publicação → consumo, e o cenário de rollback (nada é
    publicado).

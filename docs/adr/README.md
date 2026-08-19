# Architecture Decision Records (ADR)

Decisões de arquitetura do finflow. Cada ADR registra **uma** decisão, seu
contexto, alternativas descartadas e consequências. São imutáveis: quando uma
decisão muda, cria-se um novo ADR que *supersedes* o anterior — não se reescreve
o histórico.

Formato: [MADR](https://adr.github.io/madr/) simplificado (ver `0000-template.md`).

| ADR | Título | Status |
|-----|--------|--------|
| [0001](0001-politica-monetaria-e-cambio.md) | Política de valores monetários e câmbio | Accepted |
| [0002](0002-eventos-outbox-transacional-kafka.md) | Eventos de negócio via Outbox transacional + Kafka | Accepted |
| [0003](0003-aprovacao-orcamento-snapshot-idempotente.md) | Aprovação de orçamento — snapshot congelado e idempotente | Accepted |
| [0004](0004-conciliacao-bancaria-matching-por-score.md) | Conciliação bancária — matching por score, não booleano | Accepted |
| [0005](0005-autenticacao-jwt-blacklist-redis.md) | Autenticação JWT com blacklist de logout via Redis | Accepted |

## Convenção de status

`Proposed` → `Accepted` → (`Deprecated` \| `Superseded by ADR-XXXX`)

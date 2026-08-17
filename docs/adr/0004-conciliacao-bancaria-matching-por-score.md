# ADR-0004: Conciliação bancária — matching por score, não booleano

- **Status:** Accepted
- **Data:** 2026-08-17
- **Contexto de domínio:** Financeiro

## Contexto

Após upload de extrato (OFX/CSV), cada `BankTransaction` precisa ser associada
a um `Receivable` ou `Payable` existente. O casamento nunca é uma chave exata:

- Valor pode ter centavo de diferença por taxa de tarifa bancária não prevista.
- Data de crédito raramente é igual à data de vencimento (compensação, D+1/D+2).
- Descrição do extrato é texto livre do banco ("PIX RECEBIDO JOAO S"), não um
  ID de negócio.
- Múltiplos recebíveis podem ter valor e data próximos (duas parcelas de
  clientes diferentes, mesmo valor) — matching ingênuo por "primeiro que bater"
  associa a transação errada, o que é pior que não conciliar automaticamente:
  gera baixa incorreta de recebível, que é erro financeiro silencioso.

A pergunta de arquitetura é: **o que decide sozinho vs. o que vai para
revisão humana**, e como isso fica auditável.

## Decisão

### 1. Matching é scoring, não regra booleana

Cada par candidato `(BankTransaction, Receivable|Payable em aberto)` recebe um
score `0.0–1.0` combinando três sinais independentes:

| Sinal | Peso | Regra |
|-------|:----:|-------|
| Valor | 0.5 | `1.0` se exato; decai linearmente até `0.0` em ±2% de diferença |
| Data | 0.3 | `1.0` se mesma data; decai linearmente até `0.0` em ±5 dias (janela de busca é ±2 dias — fora disso nem entra como candidato) |
| Documento (CPF/CNPJ) | 0.2 | `1.0` se o documento do cliente/fornecedor aparece na descrição do extrato (regex); `0.0` caso contrário |

```java
double score = 0.5 * valueScore(tx.amount(), rec.amount())
             + 0.3 * dateScore(tx.date(), rec.dueDate())
             + 0.2 * documentScore(tx.description(), customer.document());
```

Pesos e curvas de decaimento em uma classe isolada (`ReconciliationScorer`) —
ajustáveis sem tocar no fluxo de decisão.

### 2. Só o candidato inequívoco concilia sozinho

Para cada `BankTransaction`, calculam-se scores contra todos os
`Receivable`/`Payable` em aberto na janela de ±2 dias. Decisão:

```
score do melhor candidato >= 0.85
  E (melhor score - segundo melhor score) >= 0.15
    → AUTO_RECONCILED (associa, marca Receivable.status = PAID)
  senão
    → PENDING_REVIEW (fila humana, candidatos ordenados por score)
```

A **margem** entre 1º e 2º colocado é tão importante quanto o score absoluto:
duas parcelas de R$ 500 vencendo no mesmo dia geram dois candidatos com score
alto e quase empatado — a ambiguidade é real e vai para revisão, mesmo que
cada um isoladamente pareça "bom o suficiente". Isso é o que evita a baixa
errada silenciosa citada no contexto.

Nenhuma transação some sem decisão: toda `BankTransaction` termina em
`AUTO_RECONCILED` ou `PENDING_REVIEW` — nunca "ignorada".

### 3. Fila de revisão mostra o *porquê*

`GET /bank-transactions?reconciled=false` retorna, para cada transação
pendente, os top-3 candidatos com score e o detalhamento por sinal (valor/data/
documento) — a UI não pede para o humano adivinhar, mostra a mesma informação
que o algoritmo usou. Confirmação manual (`POST /{id}/reconcile`) grava
`matched_by = MANUAL` e `matched_score` (para auditoria e para futura
calibração dos pesos).

### 4. Auditoria do automático

Toda conciliação automática grava `matched_by = AUTO`, `matched_score` e
`match_margin` na própria `BankTransaction`. Isso permite, no futuro,
auditar/recalibrar o threshold sem re-arquitetar — é dado, não decisão perdida.

## Alternativas consideradas

- **Match exato (valor + data ±2 dias, sem score).** É o que o spec original
  descrevia. Simples, mas binário: não distingue "quase certo" de "ambíguo
  entre dois candidatos igualmente válidos" — a lacuna que motiva este ADR.
  Descartado em favor de scoring com margem.
- **Machine learning (modelo treinado com conciliações passadas).** Melhoraria
  precisão com volume, mas é complexidade desproporcional ao estágio do
  projeto e não auditável linha a linha (força #3 do contexto exige
  explicabilidade). Fica como evolução futura se houver dado suficiente.
- **Sempre revisão manual (sem auto-reconcile).** Mais seguro, mas anula o
  valor do recurso — conciliação manual de centenas de transações é o problema
  que o sistema deveria resolver. Descartado.

## Consequências

- **Positivas:** decisão auditável e explicável; ambiguidade genuína vai para
  humano em vez de adivinhar; thresholds isolados e calibráveis; nenhuma
  transação "desaparece" sem status.
- **Negativas / custos:** mais complexo que match exato; thresholds (`0.85`,
  margem `0.15`) são heurísticos — exigem ajuste observando dados reais depois
  do primeiro uso.
- **Impacto em schema/código:**
  - `bank_transactions`: colunas `matched_by` (`AUTO`|`MANUAL`|`NULL`),
    `matched_score NUMERIC(4,3)`, `match_margin NUMERIC(4,3)` (Flyway V10).
  - `domain/service/ReconciliationScorer` — puro, testável por tabela de casos
    (sem I/O), cobrindo os limites das curvas de decaimento.
  - `POST /bank-transactions/auto-reconcile` roda o scorer sobre todas as
    transações não conciliadas do lote e aplica a regra do §2.
  - Teste dedicado ao caso de ambiguidade: duas parcelas idênticas em valor e
    data devem cair em `PENDING_REVIEW`, não conciliar a primeira encontrada.

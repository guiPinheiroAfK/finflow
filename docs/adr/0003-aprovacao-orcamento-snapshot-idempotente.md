# ADR-0003: Aprovação de orçamento — snapshot congelado e idempotente

- **Status:** Accepted
- **Data:** 2026-08-17
- **Contexto de domínio:** Comercial → Financeiro

## Contexto

`POST /quotes/{id}/approve` é a transição mais carregada do sistema: um
`Quote` (DRAFT/SENT) vira um `Order` (CONFIRMED) e, na mesma operação, nascem
os `Receivable`s (parcelas do cliente) e `Payable`s (por fornecedor). Duas
forças:

1. **Preço não pode se mover sob o pé da venda.** Se o preço de um `Product`
   mudar depois que a venda foi confirmada, o histórico da venda não pode
   mudar — senão o DRE de um mês passado varia quando alguém reprecifica um
   produto hoje. Isso pede **snapshot**, não referência viva.
2. **Duplo clique é o caso comum, não o raro.** Um vendedor apertando "Aprovar"
   duas vezes (rede lenta, duplo clique) não pode gerar duas Orders, dois
   conjuntos de recebíveis e dois conjuntos de pagáveis. A operação precisa ser
   **idempotente**.

## Decisão

### 1. Snapshot de preços no momento da aprovação

`OrderItem` **não referencia** `Product.salePrice`/`costPrice` em tempo real —
copia os valores no momento da aprovação:

```java
OrderItem.fromQuoteItem(quoteItem) // copia unitCost, unitSale, description, travelDate...
```

`Product` permanece como referência (para saber *o que* foi vendido), mas os
valores monetários do `OrderItem` são cópias imutáveis, calculados com a
política do [[0001-politica-monetaria-e-cambio]] (`Money`, câmbio congelado se
o custo do fornecedor for em moeda estrangeira).

### 2. Idempotência via estado da máquina + chave

Duas camadas, porque cobrem falhas diferentes:

- **Guarda de estado:** `approve()` só executa se `quote.status == SENT` (ou
  `DRAFT`, conforme regra de negócio). Ao final, `quote.status = APPROVED`.
  Uma segunda chamada encontra `status == APPROVED` e retorna o `Order` já
  existente (200, não erro) em vez de tentar aprovar de novo — a transição é
  **idempotente por construção**, não por checagem de duplicata a posteriori.
- **Constraint de unicidade:** `orders.quote_id UNIQUE` (nullable) no banco.
  Mesmo sob concorrência (duas requisições simultâneas passando a guarda de
  estado antes do commit uma da outra), o banco rejeita a segunda `Order` para
  o mesmo `quote_id`. O service captura a violação e retorna a `Order`
  existente — defesa em profundidade, não confiar só na checagem em memória.

```java
@Transactional
public Order approve(UUID quoteId) {
    Quote quote = quoteRepository.findByIdForUpdate(quoteId); // SELECT ... FOR UPDATE
    if (quote.status() == APPROVED) {
        return orderRepository.findByQuoteId(quoteId).orElseThrow(); // idempotente
    }
    quote.requireStatus(SENT, DRAFT);

    Order order = Order.fromQuote(quote);              // snapshot dos itens
    order = orderRepository.save(order);

    List<Receivable> receivables = Installments.generate(order); // ADR-0001 §5
    List<Payable> payables = Payables.generateBySupplier(order);
    receivableRepository.saveAll(receivables);
    payableRepository.saveAll(payables);

    quote.markApproved();
    events.publishEvent(new OrderConfirmed(order.id(), order.customerId(), order.totalSale()));
    return order;
}
```

`findByIdForUpdate` (lock pessimista) fecha a janela de corrida entre a leitura
do status e o commit, para o caso de duas requisições concorrentes chegarem
quase juntas na mesma `Quote`.

### 3. Uma transação, um evento

Toda a operação — mudança de status do `Quote`, criação da `Order`, geração de
`Receivable`s e `Payable`s — corre em **uma** `@Transactional`. O evento
`OrderConfirmed` é levantado dentro dela e materializado na outbox
(`BEFORE_COMMIT`, ver [[0002-eventos-outbox-transacional-kafka]]): se qualquer
parte falhar (ex.: geração de parcelas lança exceção), tudo reverte — não existe
`Order` órfã sem recebíveis.

## Alternativas consideradas

- **Token de idempotência (`Idempotency-Key` no header).** Padrão comum em APIs
  de pagamento, mas resolve um problema mais genérico (retry de rede) que aqui
  já é coberto pela guarda de estado + constraint de banco. Adicionar o header
  seria complexidade sem ganho adicional para este endpoint específico.
- **`OrderItem` referenciando `Product` por FK sem snapshot.** Mais simples,
  mas quebra a força #1 — reprecificar um produto mudaria vendas históricas.
  Descartado.
- **Checar duplicata só em memória (sem constraint no banco).** Insuficiente
  sob concorrência real (duas requisições podem passar a checagem antes de
  qualquer uma commitar). Descartado — mantemos a constraint como
  última linha de defesa.

## Consequências

- **Positivas:** histórico de vendas imutável mesmo com reprecificação de
  produtos; duplo clique/retry não duplica efeitos financeiros; auditoria clara
  (o que foi vendido é o que está gravado, não o que o produto é hoje).
- **Negativas / custos:** `OrderItem` duplica campos de `QuoteItem`/`Product`
  (aceito — é o preço da imutabilidade); lock pessimista em `approve()` serializa
  aprovações concorrentes da mesma quote (aceitável, é operação rara e rápida).
- **Impacto em schema/código:**
  - `orders.quote_id UUID UNIQUE NULL` (Flyway V7).
  - `Order.fromQuote(Quote)` / `OrderItem.fromQuoteItem(QuoteItem)` — factory
    methods de snapshot, não construtores públicos genéricos.
  - `QuoteRepository.findByIdForUpdate` com `@Lock(PESSIMISTIC_WRITE)`.
  - Teste de concorrência: duas chamadas simultâneas a `approve()` resultam em
    uma única `Order` (Testcontainers + threads).

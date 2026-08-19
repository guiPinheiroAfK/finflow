# ADR-0001: Política de valores monetários e câmbio

- **Status:** Accepted
- **Data:** 2026-08-17
- **Contexto de domínio:** Financeiro (cross-cutting — afeta Comercial e Operacional)

## Contexto

O finflow move dinheiro em quatro moedas (BRL, USD, EUR, ARS), calcula margens,
gera parcelas de recebíveis e concilia contra extratos bancários reais. Três
forças tornam a representação de dinheiro uma decisão de arquitetura, não um
detalhe de implementação:

1. **Precisão.** `double`/`float` são binários e não representam frações
   decimais exatamente (`0.1 + 0.2 != 0.3`). Em somatórios de DRE e comissões o
   erro acumula. Proibido para dinheiro.
2. **Arredondamento é acordo com o mundo externo.** A conciliação bancária
   (ADR de conciliação) compara o valor do recebível contra o valor que o banco
   registrou. Se arredondarmos diferente do banco/adquirente, um recebível de
   uma parcela pode divergir em R$ 0,01 e **nunca dar match automático**. O modo
   de arredondamento é, portanto, requisito de negócio.
3. **Câmbio é histórico.** O custo de um fornecedor em USD, uma vez confirmada a
   venda, não pode variar porque a cotação mudou amanhã. O valor em BRL e a taxa
   usada precisam ser **congelados** no momento da transação.

## Decisão

### 1. Tipo e proibições

- Todo valor monetário é `java.math.BigDecimal`. **Nunca** `double`, `float` ou
  `Double`.
- Nunca construir `BigDecimal` a partir de `double` (`new BigDecimal(0.1)`
  carrega o erro binário). Usar `BigDecimal.valueOf(...)` ou o construtor de
  `String`.
- Comparar valores com `compareTo()`, nunca `equals()` — `equals` de
  `BigDecimal` é sensível à escala (`2.0 != 2.00`).

### 2. Escalas e modo de arredondamento

| Grandeza | Escala | Exemplo |
|----------|:------:|---------|
| Valor monetário (qualquer moeda) | **2** | `1234.56` |
| Taxa de câmbio (`exchangeRate`) | **6** | `5.234100` |
| Percentuais (markup, comissão) | **4** | `0.1550` (15,5%) |

- **Modo de arredondamento único e global: `RoundingMode.HALF_UP`.**
  Justificativa: é a convenção comercial e bancária brasileira. Bancos e
  adquirentes arredondam parcelas com HALF_UP; adotar o mesmo modo garante que
  o valor persistido do recebível bata **exatamente** com o valor do extrato na
  conciliação. `HALF_EVEN` (banker's rounding) reduziria viés estatístico no
  DRE, mas introduziria divergências de centavo contra o mundo externo — o
  custo de conciliação supera o benefício contábil neste domínio.
- Todas as moedas em escopo (BRL, USD, EUR, ARS) têm 2 casas decimais de
  subunidade, então escala 2 vale para todas. Se um dia entrar moeda sem
  subunidade decimal (ex.: JPY), isto vira um novo ADR.

### 3. Value Object `Money` (`@Embeddable`)

Dinheiro nunca trafega como `BigDecimal` solto — anda sempre acompanhado da
moeda, num Value Object imutável:

```java
@Embeddable
public record Money(
    @Column(precision = 19, scale = 2) BigDecimal amount,
    @Enumerated(EnumType.STRING) Currency currency
) {
    public Money {
        amount = amount.setScale(2, RoundingMode.HALF_UP); // normaliza na construção
    }
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }
    // subtract, multiply(BigDecimal), isNegative, isZero, requireSameCurrency...
}
```

- Operações aritméticas entre moedas diferentes lançam exceção — soma de USD com
  BRL é bug, não conversão silenciosa.
- Conversão de moeda é operação **explícita** que exige uma taxa (ver §4).

### 4. Câmbio: congelar, nunca recalcular

Toda entidade que carrega valor em moeda estrangeira persiste **três** campos:

- `amount` + `currency` — valor na moeda original (o que o fornecedor cobra).
- `exchangeRate` — a cotação usada, congelada.
- `amountBrl` — `amount × exchangeRate` arredondado a 2 casas HALF_UP, congelado.

Regra invariante: **`amountBrl` e `exchangeRate` são gravados uma vez, no
momento da confirmação da transação, e jamais recalculados.** Relatórios
históricos leem o valor congelado. A tabela `exchange_rates` (Flyway V11) serve
para *obter* a cotação do dia; não é fonte para recomputar transações passadas.

```java
// na confirmação da venda / criação do pagável
BigDecimal rate = exchangeRateService.rateFor(supplier.currency(), today);
BigDecimal amountBrl = cost.amount().multiply(rate).setScale(2, RoundingMode.HALF_UP);
```

### 5. Geração de parcelas (invariante de soma)

Dividir um total em N parcelas **deve** somar exatamente o total — sem centavo
sobrando ou faltando. A última parcela absorve o resíduo:

```java
static List<BigDecimal> split(BigDecimal total, int n) {
    BigDecimal base = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
    List<BigDecimal> parts = new ArrayList<>(Collections.nCopies(n - 1, base));
    BigDecimal last = total.subtract(base.multiply(BigDecimal.valueOf(n - 1L)));
    parts.add(last);            // absorve o resíduo de arredondamento
    return parts;               // soma(parts) == total, garantido
}
// Ex.: split(100.00, 3) -> [33.33, 33.33, 33.34]
```

Esta função é testada com property-based test: para todo `total` e `n`,
`sum(split(total,n)).compareTo(total) == 0`.

### 6. Fronteiras (persistência e API)

- **Postgres:** colunas `NUMERIC(19,2)` para valores, `NUMERIC(19,6)` para taxa,
  `NUMERIC(9,4)` para percentuais. `NUMERIC` é decimal exato no Postgres.
- **JSON/API:** `BigDecimal` serializado como **string** (`"1234.56"`), não
  número — evita que o JSON parser do frontend (IEEE-754 double em JS) corrompa
  a precisão. Configuração global no Jackson
  (`WRITE_BIGDECIMAL_AS_PLAIN` + serializar como string). O frontend formata a
  partir da string.

## Alternativas consideradas

- **`long` de centavos (minor units).** Rápido e exato, mas perde a moeda no
  tipo, complica escala 6 do câmbio e a divisão de parcelas, e polui o código
  com `/100`/`*100`. `BigDecimal` + `Money` é mais expressivo e igualmente
  correto.
- **`RoundingMode.HALF_EVEN`.** Menor viés em grandes volumes contábeis, mas
  diverge do arredondamento de bancos/adquirentes → quebra conciliação
  automática. Descartado pela força #2.
- **Recalcular câmbio sob demanda** a partir de `exchange_rates`. Simplifica o
  schema mas viola a imutabilidade histórica: um relatório de março mudaria em
  abril. Descartado.

## Consequências

- **Positivas:** valores exatos ponta a ponta; conciliação bate contra o banco;
  histórico financeiro imutável; `Money` centraliza regras e impede soma
  cross-currency por acidente.
- **Negativas / custos:** `BigDecimal` é verboso e exige disciplina
  (`compareTo`, nunca `new BigDecimal(double)`); três colunas por valor
  estrangeiro; serialização como string exige tratamento no frontend.
- **Impacto em schema/código:**
  - Flyway: colunas `NUMERIC(19,2)`/`(19,6)`/`(9,4)` conforme §6 em toda tabela
    com dinheiro (`quotes`, `quote_items`, `orders`, `order_items`,
    `receivables`, `payables`, `exchange_rates`, `commissions`).
  - `payables`, `order_items` (custo em moeda estrangeira) carregam o trio
    `amount`/`currency` + `exchange_rate` + `amount_brl`.
  - Classe `com.finflow.domain.model.shared.Money` (`@Embeddable` record) +
    enum `Currency`.
  - Util `Installments.split(...)` com property-based test.
  - Config Jackson global para `BigDecimal` como string.

# ADR-0005: Autenticação JWT com blacklist de logout via Redis

- **Status:** Accepted
- **Data:** 2026-08-17
- **Contexto de domínio:** Cross-cutting (infraestrutura de segurança)

## Contexto

JWT stateless é a escolha natural para a API (sem sessão em memória, escala
horizontal), mas stateless tem um problema conhecido: **logout não existe**. Um
token assinado válido continua válido até expirar, mesmo que o usuário tenha
clicado "sair". Para um sistema financeiro (troca de vendedor, dispositivo
perdido, encerramento de sessão suspeita), isso é inaceitável sem mitigação.

A solução clássica é uma blacklist de tokens revogados — mas guardá-la em
memória do processo quebra sob múltiplas instâncias (revogar numa instância não
afeta as outras) e guardá-la sem expiração faz a lista crescer para sempre.

## Decisão

### 1. Par de tokens, vida curta no access token

- **Access token:** JWT assinado (HS256, segredo via `FINFLOW_JWT_SECRET`),
  **15 minutos** de validade. Claims: `sub` (userId), `jti` (UUID único do
  token), `roles`, `exp`, `iat`.
- **Refresh token:** JWT opaco de vida longa (7 dias), usado só em
  `POST /auth/refresh` para emitir novo par. Também carrega `jti` próprio e é
  verificado contra a blacklist.
- Vida curta do access token é a mitigação de fundo: mesmo sem blacklist, um
  token vazado expira sozinho em minutos. A blacklist cobre o intervalo entre
  logout e essa expiração natural.

### 2. Blacklist por `jti` no Redis, TTL = tempo restante do token

```java
@Service
class TokenBlacklistService {
    void revoke(String jti, Instant tokenExpiry) {
        long ttlSeconds = Duration.between(Instant.now(), tokenExpiry).toSeconds();
        if (ttlSeconds > 0) {
            redis.opsForValue().set("blacklist:jti:" + jti, "1", Duration.ofSeconds(ttlSeconds));
        }
    }
    boolean isRevoked(String jti) {
        return redis.hasKey("blacklist:jti:" + jti);
    }
}
```

- **TTL = validade restante do token, não um valor fixo.** Um token revogado
  faltando 3 minutos para expirar soma 3 minutos no Redis, não 15 — a entrada
  desaparece sozinha quando o JWT já seria inválido de qualquer forma. Evita
  que a blacklist cresça indefinidamente (autolimpeza pelo próprio Redis, sem
  job de faxina).
- `logout` revoga **os dois** `jti` (access + refresh) — refresh também precisa
  morrer, senão o usuário "deslogado" tira um novo access token no minuto
  seguinte.
- `POST /auth/refresh` **sempre** verifica o `jti` do refresh token contra a
  blacklist antes de emitir novo par — sem essa checagem o refresh token vira
  um jeito de contornar o logout.

### 3. Verificação no filtro de segurança

`JwtAuthenticationFilter` (`OncePerRequestFilter`), em ordem:

```
1. extrai e valida assinatura/expiração do JWT (rejeita se inválido -> 401)
2. lê jti do payload
3. consulta TokenBlacklistService.isRevoked(jti)  -- 1 lookup O(1) no Redis
4. se revogado -> 401; senão -> popula SecurityContext e segue a cadeia
```

O custo é um `GET` no Redis por request autenticada — aceitável (Redis já está
na stack; latência sub-milissegundo em rede local/mesmo cluster).

### 4. Rotação de segredo (consequência operacional)

Trocar `FINFLOW_JWT_SECRET` invalida todos os tokens emitidos (assinatura deixa
de bater) — isso já é uma forma de revogação em massa, útil em caso de
comprometimento do segredo. Documentado aqui para não ser redescoberto como
"bug" depois.

## Alternativas consideradas

- **Sessão stateful tradicional (Redis como session store completo).** Resolve
  logout trivialmente, mas abandona a vantagem do JWT (claims auto-contidas,
  menos round-trip). Como o sistema já paga o custo de uma consulta ao Redis
  por request na blacklist, a diferença prática é pequena — mas blacklist
  mantém o JWT como fonte de verdade das claims e o Redis só como lista de
  exceções, o que é mais barato de operar (chaves pequenas, TTL automático) do
  que sessões completas.
- **Blacklist em memória (`ConcurrentHashMap`) por instância.** Falha sob
  múltiplas instâncias — logout numa instância não revoga em outra. Descartado
  assim que há mais de um pod/processo, o que é o caso normal em produção.
- **Sem blacklist, só confiar na vida curta do token.** Aceitável para muitos
  sistemas, mas deixa uma janela de até 15 min onde "logout" é só cosmético no
  frontend — inadequado para dado financeiro. Descartado.

## Consequências

- **Positivas:** logout real e imediato; revogação funciona sob múltiplas
  instâncias (Redis compartilhado); blacklist se autolimpa via TTL, sem job de
  limpeza; refresh token também revogável, fechando o contorno óbvio.
- **Negativas / custos:** uma consulta Redis por request autenticada (latência
  extra pequena, mas não-zero); dependência de disponibilidade do Redis para
  autenticação funcionar (se Redis cair, decisão de negócio: falhar aberto
  arrisca token revogado passar; falhar fechado derruba toda autenticação —
  adotamos **falhar fechado**, consistente com o domínio financeiro).
- **Impacto em schema/código:**
  - `infrastructure/security`: `JwtService` (gera/valida, inclui `jti`),
    `TokenBlacklistService` (Redis), `JwtAuthenticationFilter`.
  - `AuthController`: `login` emite par; `refresh` valida blacklist do refresh
    antes de emitir novo par; `logout` revoga ambos os `jti` da requisição
    atual.
  - Sem migration nova — Redis é key-value, não schema relacional.
  - Teste de integração (Testcontainers Redis): login → logout → request com o
    mesmo access token → 401.

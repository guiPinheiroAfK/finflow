# finflow

> Gestão comercial e financeira para turismo e serviços

**Stack:** Spring Boot 3 · Java 21 · React 19 · TypeScript · Tailwind CSS v4 · PostgreSQL · Redis · Kafka

## Estrutura

```
finflow/
├── apps/
│   ├── backend/     ← Spring Boot 3 / Java 21
│   └── frontend/    ← React 19 + Vite + TypeScript + Tailwind
├── docker-compose.yml
└── .github/workflows/
```

## Quick Start

```bash
git clone https://github.com/guiPinheiroAfK/finflow.git
cd finflow
docker-compose up -d
# Backend:  http://localhost:8080
# Frontend: http://localhost:3000
# Swagger:  http://localhost:8080/swagger-ui.html
# Mailhog:  http://localhost:8025
```

## Domínios

| Domínio | Responsabilidade |
|---------|-----------------|
| Comercial | Clientes, Orçamentos, Vendas, Comissões |
| Financeiro | Contas a Pagar/Receber, Conciliação Bancária, Fluxo de Caixa |
| Operacional | Fornecedores, Produtos/Serviços, Câmbio |
| Relatórios | DRE mensal, Vendas, Comissões, Inadimplência |

## Roadmap

- [ ] Fase 1 — Foundation: Auth JWT + CRUD base + Setup React
- [ ] Fase 2 — Comercial: Orçamentos + Vendas
- [ ] Fase 3 — Financeiro: Contas + Conciliação bancária
- [ ] Fase 4 — Relatórios + Dashboard + CI/CD

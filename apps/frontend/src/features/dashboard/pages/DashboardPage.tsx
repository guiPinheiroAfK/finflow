import { useDashboardSummary } from '@/features/dashboard/hooks/useDashboardSummary'
import { KpiCard } from '@/features/dashboard/components/KpiCard'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

export function DashboardPage() {
  const { data, isLoading, isError } = useDashboardSummary()

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Dashboard</h1>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar os indicadores.</p>}

      {data && (
        <div className="grid grid-cols-2 gap-4 md:grid-cols-3">
          <KpiCard
            title="A receber em aberto"
            value={formatMoney(data.openReceivablesAmount)}
            hint={`${data.openReceivablesCount} recebível(is)`}
          />
          <KpiCard
            title="Recebíveis vencidos"
            value={String(data.overdueReceivablesCount)}
            tone={data.overdueReceivablesCount > 0 ? 'warning' : 'default'}
          />
          <KpiCard
            title="A pagar pendente"
            value={formatMoney(data.openPayablesAmount)}
            hint={`${data.openPayablesCount} pagável(is)`}
          />
          <KpiCard title="Orçamentos em aberto" value={String(data.pendingQuotesCount)} hint="rascunho + enviado" />
          <KpiCard title="Vendas confirmadas" value={String(data.confirmedOrdersCount)} tone="success" hint="aguardando emissão" />
        </div>
      )}
    </div>
  )
}

import { useState } from 'react'
import { useReceivables } from '@/features/receivables/hooks/useReceivables'
import { ReceivableList } from '@/features/receivables/components/ReceivableList'
import type { ReceivableStatus } from '@/features/receivables/types/receivable.types'

const STATUS_OPTIONS: { value: ReceivableStatus | ''; label: string }[] = [
  { value: '', label: 'Todos' },
  { value: 'PENDING', label: 'Pendente' },
  { value: 'PARTIAL', label: 'Parcial' },
  { value: 'PAID', label: 'Pago' },
  { value: 'OVERDUE', label: 'Vencido' },
  { value: 'CANCELLED', label: 'Cancelado' },
]

export function ReceivablesPage() {
  const [status, setStatus] = useState<ReceivableStatus | ''>('')
  const { data, isLoading, isError } = useReceivables(status || undefined)

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Contas a receber</h1>

      <select
        className="h-9 w-48 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
        value={status}
        onChange={(e) => setStatus(e.target.value as ReceivableStatus | '')}
      >
        {STATUS_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar recebíveis.</p>}
      {data && <ReceivableList receivables={data.content} />}
    </div>
  )
}

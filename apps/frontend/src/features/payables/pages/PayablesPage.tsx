import { useState } from 'react'
import { usePayables } from '@/features/payables/hooks/usePayables'
import { PayableList } from '@/features/payables/components/PayableList'
import type { PayableStatus } from '@/features/payables/types/payable.types'

const STATUS_OPTIONS: { value: PayableStatus | ''; label: string }[] = [
  { value: '', label: 'Todos' },
  { value: 'PENDING', label: 'Pendente' },
  { value: 'PAID', label: 'Pago' },
  { value: 'CANCELLED', label: 'Cancelado' },
]

export function PayablesPage() {
  const [status, setStatus] = useState<PayableStatus | ''>('')
  const { data, isLoading, isError } = usePayables(status || undefined)

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Contas a pagar</h1>

      <select
        className="h-9 w-48 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
        value={status}
        onChange={(e) => setStatus(e.target.value as PayableStatus | '')}
      >
        {STATUS_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar pagáveis.</p>}
      {data && <PayableList payables={data.content} />}
    </div>
  )
}

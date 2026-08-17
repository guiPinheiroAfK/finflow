import type { ReceivableStatus } from '@/features/receivables/types/receivable.types'

const STATUS_STYLES: Record<ReceivableStatus, string> = {
  PENDING: 'bg-[var(--muted)] text-[var(--muted-foreground)]',
  PARTIAL: 'bg-amber-500/15 text-amber-600 dark:text-amber-400',
  PAID: 'bg-green-500/15 text-green-600 dark:text-green-400',
  OVERDUE: 'bg-red-500/15 text-red-600 dark:text-red-400',
  CANCELLED: 'bg-[var(--muted)] text-[var(--muted-foreground)] line-through',
}

const STATUS_LABELS: Record<ReceivableStatus, string> = {
  PENDING: 'Pendente',
  PARTIAL: 'Parcial',
  PAID: 'Pago',
  OVERDUE: 'Vencido',
  CANCELLED: 'Cancelado',
}

export function ReceivableStatusBadge({ status }: { status: ReceivableStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STATUS_STYLES[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  )
}

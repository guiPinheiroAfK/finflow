import type { PayableStatus } from '@/features/payables/types/payable.types'

const STATUS_STYLES: Record<PayableStatus, string> = {
  PENDING: 'bg-[var(--muted)] text-[var(--muted-foreground)]',
  PAID: 'bg-green-500/15 text-green-600 dark:text-green-400',
  CANCELLED: 'bg-[var(--muted)] text-[var(--muted-foreground)] line-through',
}

const STATUS_LABELS: Record<PayableStatus, string> = {
  PENDING: 'Pendente',
  PAID: 'Pago',
  CANCELLED: 'Cancelado',
}

export function PayableStatusBadge({ status }: { status: PayableStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STATUS_STYLES[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  )
}

import type { OrderStatus } from '@/features/orders/types/order.types'

const STATUS_STYLES: Record<OrderStatus, string> = {
  CONFIRMED: 'bg-blue-500/15 text-blue-600 dark:text-blue-400',
  ISSUED: 'bg-green-500/15 text-green-600 dark:text-green-400',
  CANCELLED: 'bg-red-500/15 text-red-600 dark:text-red-400',
  COMPLETED: 'bg-[var(--muted)] text-[var(--muted-foreground)]',
}

const STATUS_LABELS: Record<OrderStatus, string> = {
  CONFIRMED: 'Confirmada',
  ISSUED: 'Emitida',
  CANCELLED: 'Cancelada',
  COMPLETED: 'Concluída',
}

export function OrderStatusBadge({ status }: { status: OrderStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STATUS_STYLES[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  )
}

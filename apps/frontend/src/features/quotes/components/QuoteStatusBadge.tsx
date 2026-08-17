import type { QuoteStatus } from '@/features/quotes/types/quote.types'

const STATUS_STYLES: Record<QuoteStatus, string> = {
  DRAFT: 'bg-[var(--muted)] text-[var(--muted-foreground)]',
  SENT: 'bg-blue-500/15 text-blue-600 dark:text-blue-400',
  APPROVED: 'bg-green-500/15 text-green-600 dark:text-green-400',
  REJECTED: 'bg-red-500/15 text-red-600 dark:text-red-400',
  EXPIRED: 'bg-orange-500/15 text-orange-600 dark:text-orange-400',
}

const STATUS_LABELS: Record<QuoteStatus, string> = {
  DRAFT: 'Rascunho',
  SENT: 'Enviado',
  APPROVED: 'Aprovado',
  REJECTED: 'Rejeitado',
  EXPIRED: 'Expirado',
}

export function QuoteStatusBadge({ status }: { status: QuoteStatus }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${STATUS_STYLES[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  )
}

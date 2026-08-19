import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuotes } from '@/features/quotes/hooks/useQuotes'
import { QuoteList } from '@/features/quotes/components/QuoteList'
import { buttonVariants } from '@/shared/components/ui/button'
import type { QuoteStatus } from '@/features/quotes/types/quote.types'

const STATUS_OPTIONS: { value: QuoteStatus | ''; label: string }[] = [
  { value: '', label: 'Todos' },
  { value: 'DRAFT', label: 'Rascunho' },
  { value: 'SENT', label: 'Enviado' },
  { value: 'APPROVED', label: 'Aprovado' },
  { value: 'REJECTED', label: 'Rejeitado' },
  { value: 'EXPIRED', label: 'Expirado' },
]

export function QuotesPage() {
  const [status, setStatus] = useState<QuoteStatus | ''>('')
  const { data, isLoading, isError } = useQuotes(status || undefined)

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Orçamentos</h1>
        <Link to="/quotes/new" className={buttonVariants()}>
          Novo orçamento
        </Link>
      </div>

      <select
        className="h-9 w-48 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
        value={status}
        onChange={(e) => setStatus(e.target.value as QuoteStatus | '')}
      >
        {STATUS_OPTIONS.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar orçamentos.</p>}
      {data && <QuoteList quotes={data.content} />}
    </div>
  )
}

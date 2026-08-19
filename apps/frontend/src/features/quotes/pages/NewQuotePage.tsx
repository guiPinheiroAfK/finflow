import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { QuoteForm } from '@/features/quotes/components/QuoteForm'
import { useCreateQuote } from '@/features/quotes/hooks/useCreateQuote'
import type { QuoteInput } from '@/features/quotes/schemas/quote.schema'

export function NewQuotePage() {
  const navigate = useNavigate()
  const createQuote = useCreateQuote()

  function onSubmit(input: QuoteInput) {
    createQuote.mutate(input, {
      onSuccess: (quote) => {
        toast.success('Orçamento criado com sucesso')
        navigate(`/quotes/${quote.id}`)
      },
      onError: () => toast.error('Não foi possível criar o orçamento'),
    })
  }

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Novo orçamento</h1>
      <QuoteForm onSubmit={onSubmit} isSubmitting={createQuote.isPending} />
    </div>
  )
}

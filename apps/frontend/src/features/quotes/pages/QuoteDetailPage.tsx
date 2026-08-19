import { useParams, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { useQuote } from '@/features/quotes/hooks/useQuote'
import { useSendQuote } from '@/features/quotes/hooks/useSendQuote'
import { useApproveQuote } from '@/features/quotes/hooks/useApproveQuote'
import { QuoteStatusBadge } from '@/features/quotes/components/QuoteStatusBadge'
import { ApproveQuoteForm } from '@/features/quotes/components/ApproveQuoteForm'
import { Button } from '@/shared/components/ui/button'
import type { ApproveQuoteInput } from '@/features/quotes/schemas/approve.schema'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

export function QuoteDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: quote, isLoading, isError } = useQuote(id ?? '')
  const sendQuote = useSendQuote()
  const approveQuote = useApproveQuote(id ?? '')

  if (isLoading) return <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>
  if (isError || !quote) return <p className="text-sm text-[var(--destructive)]">Orçamento não encontrado.</p>

  function handleSend() {
    if (!quote) return
    sendQuote.mutate(quote.id, {
      onSuccess: () => toast.success('Orçamento enviado ao cliente'),
      onError: () => toast.error('Não foi possível enviar o orçamento'),
    })
  }

  function handleApprove(input: ApproveQuoteInput) {
    approveQuote.mutate(input, {
      onSuccess: (order) => {
        toast.success('Orçamento aprovado -- venda gerada')
        navigate(`/orders/${order.id}`)
      },
      onError: () => toast.error('Não foi possível aprovar o orçamento'),
    })
  }

  const canSend = quote.status === 'DRAFT'
  const canApprove = quote.status === 'DRAFT' || quote.status === 'SENT'

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{quote.quoteNumber}</h1>
          <p className="text-sm text-[var(--muted-foreground)]">{quote.customerName} · {quote.sellerName}</p>
        </div>
        <QuoteStatusBadge status={quote.status} />
      </div>

      <div className="grid grid-cols-3 gap-4 rounded-lg border border-[var(--border)] p-4">
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Custo total</p>
          <p className="text-lg font-semibold">{formatMoney(quote.totalCost)}</p>
        </div>
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Venda total</p>
          <p className="text-lg font-semibold">{formatMoney(quote.totalSale)}</p>
        </div>
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Margem</p>
          <p className="text-lg font-semibold">{formatMoney(quote.margin)}</p>
        </div>
      </div>

      <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
        <table className="w-full text-sm">
          <thead className="bg-[var(--muted)] text-left">
            <tr>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Produto</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Descrição</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Qtd.</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Custo unit.</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Venda unit.</th>
            </tr>
          </thead>
          <tbody>
            {quote.items.map((item) => (
              <tr key={item.id} className="border-t border-[var(--border)]">
                <td className="px-4 py-2">{item.productName}</td>
                <td className="px-4 py-2">{item.description ?? '—'}</td>
                <td className="px-4 py-2">{item.quantity}</td>
                <td className="px-4 py-2">{formatMoney(item.unitCost)}</td>
                <td className="px-4 py-2">{formatMoney(item.unitSale)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {(canSend || canApprove) && (
        <div className="flex flex-col gap-3">
          <h2 className="text-sm font-semibold">Ações</h2>
          <div className="flex flex-wrap items-start gap-3">
            {canSend && (
              <Button variant="outline" onClick={handleSend} disabled={sendQuote.isPending}>
                {sendQuote.isPending ? 'Enviando...' : 'Enviar ao cliente'}
              </Button>
            )}
            {canApprove && <ApproveQuoteForm onSubmit={handleApprove} isSubmitting={approveQuote.isPending} />}
          </div>
        </div>
      )}
    </div>
  )
}

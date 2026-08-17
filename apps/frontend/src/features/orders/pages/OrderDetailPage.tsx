import { useParams } from 'react-router-dom'
import { toast } from 'sonner'
import { useOrder } from '@/features/orders/hooks/useOrder'
import { useCancelOrder, useIssueOrder } from '@/features/orders/hooks/useOrderActions'
import { OrderStatusBadge } from '@/features/orders/components/OrderStatusBadge'
import { Button } from '@/shared/components/ui/button'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

export function OrderDetailPage() {
  const { id } = useParams<{ id: string }>()
  const { data: order, isLoading, isError } = useOrder(id ?? '')
  const issueOrder = useIssueOrder()
  const cancelOrder = useCancelOrder()

  if (isLoading) return <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>
  if (isError || !order) return <p className="text-sm text-[var(--destructive)]">Venda não encontrada.</p>

  function handleIssue() {
    if (!order) return
    issueOrder.mutate(order.id, {
      onSuccess: () => toast.success('Venda emitida'),
      onError: () => toast.error('Não foi possível emitir a venda'),
    })
  }

  function handleCancel() {
    if (!order) return
    cancelOrder.mutate(order.id, {
      onSuccess: () => toast.success('Venda cancelada'),
      onError: () => toast.error('Não foi possível cancelar a venda'),
    })
  }

  const canIssue = order.status === 'CONFIRMED'
  const canCancel = order.status === 'CONFIRMED' || order.status === 'ISSUED'

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">{order.orderNumber}</h1>
          <p className="text-sm text-[var(--muted-foreground)]">{order.customerName} · {order.sellerName}</p>
        </div>
        <OrderStatusBadge status={order.status} />
      </div>

      <div className="grid grid-cols-4 gap-4 rounded-lg border border-[var(--border)] p-4">
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Custo total (BRL)</p>
          <p className="text-lg font-semibold">{formatMoney(order.totalCost)}</p>
        </div>
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Venda total</p>
          <p className="text-lg font-semibold">{formatMoney(order.totalSale)}</p>
        </div>
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Margem bruta</p>
          <p className="text-lg font-semibold">{formatMoney(order.grossMargin)}</p>
        </div>
        <div>
          <p className="text-xs text-[var(--muted-foreground)]">Parcelas</p>
          <p className="text-lg font-semibold">{order.installments}x</p>
        </div>
      </div>

      <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
        <table className="w-full text-sm">
          <thead className="bg-[var(--muted)] text-left">
            <tr>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Produto</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Qtd.</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Custo unit.</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Moeda</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Custo BRL (congelado)</th>
              <th className="px-4 py-2 font-medium text-[var(--muted-foreground)]">Venda unit.</th>
            </tr>
          </thead>
          <tbody>
            {order.items.map((item) => (
              <tr key={item.id} className="border-t border-[var(--border)]">
                <td className="px-4 py-2">{item.productName}</td>
                <td className="px-4 py-2">{item.quantity}</td>
                <td className="px-4 py-2">{item.unitCost}</td>
                <td className="px-4 py-2">{item.unitCostCurrency}</td>
                <td className="px-4 py-2">{formatMoney(item.unitCostBrl)}</td>
                <td className="px-4 py-2">{formatMoney(item.unitSale)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {(canIssue || canCancel) && (
        <div className="flex flex-col gap-3">
          <h2 className="text-sm font-semibold">Ações</h2>
          <div className="flex gap-3">
            {canIssue && (
              <Button onClick={handleIssue} disabled={issueOrder.isPending}>
                {issueOrder.isPending ? 'Emitindo...' : 'Emitir'}
              </Button>
            )}
            {canCancel && (
              <Button variant="destructive" onClick={handleCancel} disabled={cancelOrder.isPending}>
                {cancelOrder.isPending ? 'Cancelando...' : 'Cancelar venda'}
              </Button>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

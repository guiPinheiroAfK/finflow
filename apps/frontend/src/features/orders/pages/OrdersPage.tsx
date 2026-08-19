import { useOrders } from '@/features/orders/hooks/useOrders'
import { OrderList } from '@/features/orders/components/OrderList'

export function OrdersPage() {
  const { data, isLoading, isError } = useOrders()

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Vendas</h1>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar vendas.</p>}
      {data && <OrderList orders={data.content} />}
    </div>
  )
}

import { useQuery } from '@tanstack/react-query'
import { getOrders } from '@/features/orders/api/orders.api'

export function useOrders() {
  return useQuery({
    queryKey: ['orders'],
    queryFn: getOrders,
  })
}

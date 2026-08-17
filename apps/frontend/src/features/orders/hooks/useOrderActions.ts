import { useMutation, useQueryClient } from '@tanstack/react-query'
import { cancelOrder, issueOrder } from '@/features/orders/api/orders.api'

export function useIssueOrder() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: issueOrder,
    onSuccess: (order) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.setQueryData(['orders', order.id], order)
    },
  })
}

export function useCancelOrder() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: cancelOrder,
    onSuccess: (order) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] })
      queryClient.setQueryData(['orders', order.id], order)
    },
  })
}

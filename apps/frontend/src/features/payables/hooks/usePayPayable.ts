import { useMutation, useQueryClient } from '@tanstack/react-query'
import { payPayable } from '@/features/payables/api/payables.api'

export function usePayPayable() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: payPayable,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['payables'] })
    },
  })
}

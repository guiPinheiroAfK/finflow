import { useMutation, useQueryClient } from '@tanstack/react-query'
import { payReceivable } from '@/features/receivables/api/receivables.api'

export function usePayReceivable() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, amount }: { id: string; amount: number }) => payReceivable(id, amount),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['receivables'] })
    },
  })
}

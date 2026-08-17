import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createQuote } from '@/features/quotes/api/quotes.api'

export function useCreateQuote() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createQuote,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quotes'] })
    },
  })
}

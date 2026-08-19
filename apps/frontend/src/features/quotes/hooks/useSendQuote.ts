import { useMutation, useQueryClient } from '@tanstack/react-query'
import { sendQuote } from '@/features/quotes/api/quotes.api'

export function useSendQuote() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: sendQuote,
    onSuccess: (quote) => {
      queryClient.invalidateQueries({ queryKey: ['quotes'] })
      queryClient.setQueryData(['quotes', quote.id], quote)
    },
  })
}

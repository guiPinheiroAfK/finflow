import { useMutation, useQueryClient } from '@tanstack/react-query'
import { approveQuote } from '@/features/quotes/api/quotes.api'
import type { ApproveQuoteInput } from '@/features/quotes/schemas/approve.schema'

export function useApproveQuote(quoteId: string) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (input: ApproveQuoteInput) => approveQuote(quoteId, input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['quotes'] })
      queryClient.invalidateQueries({ queryKey: ['orders'] })
    },
  })
}

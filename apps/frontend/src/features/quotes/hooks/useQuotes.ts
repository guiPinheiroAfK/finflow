import { useQuery } from '@tanstack/react-query'
import { getQuotes } from '@/features/quotes/api/quotes.api'
import type { QuoteStatus } from '@/features/quotes/types/quote.types'

export function useQuotes(status?: QuoteStatus) {
  return useQuery({
    queryKey: ['quotes', status ?? ''],
    queryFn: () => getQuotes(status),
  })
}

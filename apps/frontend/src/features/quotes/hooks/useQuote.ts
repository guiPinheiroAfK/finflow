import { useQuery } from '@tanstack/react-query'
import { getQuote } from '@/features/quotes/api/quotes.api'

export function useQuote(id: string) {
  return useQuery({
    queryKey: ['quotes', id],
    queryFn: () => getQuote(id),
    enabled: Boolean(id),
  })
}

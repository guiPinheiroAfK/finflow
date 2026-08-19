import { useQuery } from '@tanstack/react-query'
import { getReceivables } from '@/features/receivables/api/receivables.api'
import type { ReceivableStatus } from '@/features/receivables/types/receivable.types'

export function useReceivables(status?: ReceivableStatus) {
  return useQuery({
    queryKey: ['receivables', status ?? ''],
    queryFn: () => getReceivables(status),
  })
}

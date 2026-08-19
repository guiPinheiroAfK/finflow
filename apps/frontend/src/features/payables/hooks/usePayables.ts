import { useQuery } from '@tanstack/react-query'
import { getPayables } from '@/features/payables/api/payables.api'
import type { PayableStatus } from '@/features/payables/types/payable.types'

export function usePayables(status?: PayableStatus) {
  return useQuery({
    queryKey: ['payables', status ?? ''],
    queryFn: () => getPayables(status),
  })
}

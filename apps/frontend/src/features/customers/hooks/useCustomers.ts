import { useQuery } from '@tanstack/react-query'
import { getCustomers } from '@/features/customers/api/customers.api'

export function useCustomers(nameFilter?: string) {
  return useQuery({
    queryKey: ['customers', nameFilter ?? ''],
    queryFn: () => getCustomers(nameFilter),
  })
}

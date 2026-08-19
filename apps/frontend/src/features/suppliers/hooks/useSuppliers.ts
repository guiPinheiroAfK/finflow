import { useQuery } from '@tanstack/react-query'
import { getSuppliers } from '@/features/suppliers/api/suppliers.api'

export function useSuppliers(nameFilter?: string) {
  return useQuery({
    queryKey: ['suppliers', nameFilter ?? ''],
    queryFn: () => getSuppliers(nameFilter),
  })
}

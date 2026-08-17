import { useQuery } from '@tanstack/react-query'
import { getProducts } from '@/features/products/api/products.api'

export function useProducts(nameFilter?: string) {
  return useQuery({
    queryKey: ['products', nameFilter ?? ''],
    queryFn: () => getProducts(nameFilter),
  })
}

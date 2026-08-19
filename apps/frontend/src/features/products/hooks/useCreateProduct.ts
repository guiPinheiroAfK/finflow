import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createProduct } from '@/features/products/api/products.api'

export function useCreateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['products'] })
    },
  })
}

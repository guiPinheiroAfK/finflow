import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createSupplier } from '@/features/suppliers/api/suppliers.api'

export function useCreateSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createSupplier,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suppliers'] })
    },
  })
}

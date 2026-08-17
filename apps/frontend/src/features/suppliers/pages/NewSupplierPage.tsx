import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { SupplierForm } from '@/features/suppliers/components/SupplierForm'
import { useCreateSupplier } from '@/features/suppliers/hooks/useCreateSupplier'
import type { SupplierInput } from '@/features/suppliers/schemas/supplier.schema'

export function NewSupplierPage() {
  const navigate = useNavigate()
  const createSupplier = useCreateSupplier()

  function onSubmit(input: SupplierInput) {
    createSupplier.mutate(input, {
      onSuccess: () => {
        toast.success('Fornecedor cadastrado com sucesso')
        navigate('/suppliers')
      },
      onError: () => toast.error('Não foi possível cadastrar o fornecedor'),
    })
  }

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Novo fornecedor</h1>
      <SupplierForm onSubmit={onSubmit} isSubmitting={createSupplier.isPending} />
    </div>
  )
}

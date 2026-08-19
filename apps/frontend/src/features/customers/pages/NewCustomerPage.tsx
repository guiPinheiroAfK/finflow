import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { CustomerForm } from '@/features/customers/components/CustomerForm'
import { useCreateCustomer } from '@/features/customers/hooks/useCreateCustomer'
import type { CustomerInput } from '@/features/customers/schemas/customer.schema'

export function NewCustomerPage() {
  const navigate = useNavigate()
  const createCustomer = useCreateCustomer()

  function onSubmit(input: CustomerInput) {
    createCustomer.mutate(input, {
      onSuccess: () => {
        toast.success('Cliente cadastrado com sucesso')
        navigate('/customers')
      },
      onError: () => toast.error('Não foi possível cadastrar o cliente'),
    })
  }

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Novo cliente</h1>
      <CustomerForm onSubmit={onSubmit} isSubmitting={createCustomer.isPending} />
    </div>
  )
}

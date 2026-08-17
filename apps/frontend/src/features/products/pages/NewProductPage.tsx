import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { ProductForm } from '@/features/products/components/ProductForm'
import { useCreateProduct } from '@/features/products/hooks/useCreateProduct'
import type { ProductInput } from '@/features/products/schemas/product.schema'

export function NewProductPage() {
  const navigate = useNavigate()
  const createProduct = useCreateProduct()

  function onSubmit(input: ProductInput) {
    createProduct.mutate(input, {
      onSuccess: () => {
        toast.success('Produto cadastrado com sucesso')
        navigate('/products')
      },
      onError: () => toast.error('Não foi possível cadastrar o produto'),
    })
  }

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Novo produto</h1>
      <ProductForm onSubmit={onSubmit} isSubmitting={createProduct.isPending} />
    </div>
  )
}

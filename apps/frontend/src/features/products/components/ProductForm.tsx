import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { productSchema, type ProductInput } from '@/features/products/schemas/product.schema'
import { useSuppliers } from '@/features/suppliers/hooks/useSuppliers'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import { Label } from '@/shared/components/ui/label'

export function ProductForm({
  onSubmit,
  isSubmitting,
}: {
  onSubmit: (input: ProductInput) => void
  isSubmitting: boolean
}) {
  const { data: suppliers } = useSuppliers()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductInput>({
    resolver: zodResolver(productSchema),
    defaultValues: { category: 'PACOTE', currency: 'BRL', costPrice: 0, salePrice: 0 },
  })

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="name">Nome</Label>
          <Input id="name" {...register('name')} />
          {errors.name && <p className="text-xs text-[var(--destructive)]">{errors.name.message}</p>}
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="category">Categoria</Label>
          <select
            id="category"
            className="h-9 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
            {...register('category')}
          >
            <option value="PACOTE">Pacote</option>
            <option value="PASSAGEM">Passagem</option>
            <option value="HOSPEDAGEM">Hospedagem</option>
            <option value="TRANSFER">Transfer</option>
            <option value="SEGURO">Seguro</option>
            <option value="INGRESSO">Ingresso</option>
          </select>
        </div>
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="supplierId">Fornecedor</Label>
        <select
          id="supplierId"
          className="h-9 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
          {...register('supplierId')}
        >
          <option value="">Selecione...</option>
          {suppliers?.content.map((supplier) => (
            <option key={supplier.id} value={supplier.id}>
              {supplier.name}
            </option>
          ))}
        </select>
        {errors.supplierId && <p className="text-xs text-[var(--destructive)]">{errors.supplierId.message}</p>}
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="costPrice">Preço de custo</Label>
          <Input id="costPrice" type="number" step="0.01" min={0} {...register('costPrice', { valueAsNumber: true })} />
          {errors.costPrice && <p className="text-xs text-[var(--destructive)]">{errors.costPrice.message}</p>}
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="salePrice">Preço de venda</Label>
          <Input id="salePrice" type="number" step="0.01" min={0} {...register('salePrice', { valueAsNumber: true })} />
          {errors.salePrice && <p className="text-xs text-[var(--destructive)]">{errors.salePrice.message}</p>}
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="currency">Moeda</Label>
          <select
            id="currency"
            className="h-9 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
            {...register('currency')}
          >
            <option value="BRL">BRL</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
            <option value="ARS">ARS</option>
          </select>
        </div>
      </div>

      <Button type="submit" disabled={isSubmitting} className="mt-2 self-start">
        {isSubmitting ? 'Salvando...' : 'Salvar produto'}
      </Button>
    </form>
  )
}

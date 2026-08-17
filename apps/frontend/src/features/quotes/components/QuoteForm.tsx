import { useFieldArray, useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { quoteSchema, type QuoteInput } from '@/features/quotes/schemas/quote.schema'
import { useCustomers } from '@/features/customers/hooks/useCustomers'
import { useProducts } from '@/features/products/hooks/useProducts'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import { Label } from '@/shared/components/ui/label'

const emptyItem = { productId: '', description: '', quantity: 1, unitCost: 0, unitSale: 0, travelDate: '' }

export function QuoteForm({
  onSubmit,
  isSubmitting,
}: {
  onSubmit: (input: QuoteInput) => void
  isSubmitting: boolean
}) {
  const { data: customers } = useCustomers()
  const { data: products } = useProducts()

  const {
    register,
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<QuoteInput>({
    resolver: zodResolver(quoteSchema),
    defaultValues: { customerId: '', items: [emptyItem] },
  })

  const { fields, append, remove } = useFieldArray({ control, name: 'items' })

  function onProductChange(index: number, productId: string) {
    const product = products?.content.find((p) => p.id === productId)
    if (product) {
      setValue(`items.${index}.unitCost`, Number(product.costPrice))
      setValue(`items.${index}.unitSale`, Number(product.salePrice))
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-6">
      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="customerId">Cliente</Label>
          <select
            id="customerId"
            className="h-9 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
            {...register('customerId')}
          >
            <option value="">Selecione...</option>
            {customers?.content.map((customer) => (
              <option key={customer.id} value={customer.id}>
                {customer.name}
              </option>
            ))}
          </select>
          {errors.customerId && <p className="text-xs text-[var(--destructive)]">{errors.customerId.message}</p>}
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="validUntil">Válido até</Label>
          <Input id="validUntil" type="date" {...register('validUntil')} />
        </div>
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="notes">Observações</Label>
        <textarea
          id="notes"
          rows={2}
          className="rounded-md border border-[var(--input)] bg-transparent px-3 py-2 text-sm"
          {...register('notes')}
        />
      </div>

      <div className="flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold">Itens</h2>
          <Button type="button" variant="outline" onClick={() => append(emptyItem)}>
            Adicionar item
          </Button>
        </div>
        {errors.items?.message && <p className="text-xs text-[var(--destructive)]">{errors.items.message}</p>}

        {fields.map((field, index) => (
          <div key={field.id} className="grid grid-cols-12 gap-2 rounded-lg border border-[var(--border)] p-3">
            <div className="col-span-3 flex flex-col gap-1">
              <Label>Produto</Label>
              <select
                className="h-9 rounded-md border border-[var(--input)] bg-transparent px-2 text-sm"
                {...register(`items.${index}.productId`, {
                  onChange: (e) => onProductChange(index, e.target.value),
                })}
              >
                <option value="">Selecione...</option>
                {products?.content.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name}
                  </option>
                ))}
              </select>
              {errors.items?.[index]?.productId && (
                <p className="text-xs text-[var(--destructive)]">{errors.items[index]?.productId?.message}</p>
              )}
            </div>
            <div className="col-span-3 flex flex-col gap-1">
              <Label>Descrição</Label>
              <Input {...register(`items.${index}.description`)} />
            </div>
            <div className="col-span-1 flex flex-col gap-1">
              <Label>Qtd.</Label>
              <Input type="number" min={1} {...register(`items.${index}.quantity`, { valueAsNumber: true })} />
            </div>
            <div className="col-span-2 flex flex-col gap-1">
              <Label>Custo unit.</Label>
              <Input type="number" step="0.01" min={0} {...register(`items.${index}.unitCost`, { valueAsNumber: true })} />
            </div>
            <div className="col-span-2 flex flex-col gap-1">
              <Label>Venda unit.</Label>
              <Input type="number" step="0.01" min={0} {...register(`items.${index}.unitSale`, { valueAsNumber: true })} />
            </div>
            <div className="col-span-1 flex items-end">
              <Button type="button" variant="ghost" onClick={() => remove(index)} disabled={fields.length === 1}>
                Remover
              </Button>
            </div>
          </div>
        ))}
      </div>

      <Button type="submit" disabled={isSubmitting} className="self-start">
        {isSubmitting ? 'Salvando...' : 'Salvar orçamento'}
      </Button>
    </form>
  )
}

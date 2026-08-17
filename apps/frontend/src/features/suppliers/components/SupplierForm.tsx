import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { supplierSchema, type SupplierInput } from '@/features/suppliers/schemas/supplier.schema'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import { Label } from '@/shared/components/ui/label'

export function SupplierForm({
  onSubmit,
  isSubmitting,
}: {
  onSubmit: (input: SupplierInput) => void
  isSubmitting: boolean
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SupplierInput>({
    resolver: zodResolver(supplierSchema),
    defaultValues: { category: 'OUTRO', currency: 'BRL', paymentTermDays: 0 },
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
            <option value="HOTEL">Hotel</option>
            <option value="AEREA">Aérea</option>
            <option value="TRANSFER">Transfer</option>
            <option value="PASSEIO">Passeio</option>
            <option value="SEGURO">Seguro</option>
            <option value="OUTRO">Outro</option>
          </select>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="contactName">Contato</Label>
          <Input id="contactName" {...register('contactName')} />
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="email">E-mail</Label>
          <Input id="email" type="email" {...register('email')} />
          {errors.email && <p className="text-xs text-[var(--destructive)]">{errors.email.message}</p>}
        </div>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="document">Documento</Label>
          <Input id="document" {...register('document')} />
        </div>
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="paymentTermDays">Prazo de pagamento (dias)</Label>
          <Input id="paymentTermDays" type="number" min={0} {...register('paymentTermDays', { valueAsNumber: true })} />
          {errors.paymentTermDays && (
            <p className="text-xs text-[var(--destructive)]">{errors.paymentTermDays.message}</p>
          )}
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
        {isSubmitting ? 'Salvando...' : 'Salvar fornecedor'}
      </Button>
    </form>
  )
}

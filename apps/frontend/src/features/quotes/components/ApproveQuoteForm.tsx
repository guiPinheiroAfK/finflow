import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { approveQuoteSchema, type ApproveQuoteInput } from '@/features/quotes/schemas/approve.schema'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import { Label } from '@/shared/components/ui/label'

export function ApproveQuoteForm({
  onSubmit,
  isSubmitting,
}: {
  onSubmit: (input: ApproveQuoteInput) => void
  isSubmitting: boolean
}) {
  const { register, handleSubmit } = useForm<ApproveQuoteInput>({
    resolver: zodResolver(approveQuoteSchema),
    defaultValues: { paymentMethod: 'PIX', installments: 1 },
  })

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex items-end gap-3 rounded-lg border border-[var(--border)] p-3">
      <div className="flex flex-col gap-1">
        <Label htmlFor="paymentMethod">Forma de pagamento</Label>
        <select
          id="paymentMethod"
          className="h-9 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
          {...register('paymentMethod')}
        >
          <option value="DINHEIRO">Dinheiro</option>
          <option value="CARTAO_CREDITO">Cartão de crédito</option>
          <option value="CARTAO_DEBITO">Cartão de débito</option>
          <option value="PIX">PIX</option>
          <option value="BOLETO">Boleto</option>
          <option value="TRANSFERENCIA">Transferência</option>
        </select>
      </div>
      <div className="flex flex-col gap-1">
        <Label htmlFor="installments">Parcelas</Label>
        <Input id="installments" type="number" min={1} className="w-24"
               {...register('installments', { valueAsNumber: true })} />
      </div>
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Aprovando...' : 'Aprovar orçamento'}
      </Button>
    </form>
  )
}

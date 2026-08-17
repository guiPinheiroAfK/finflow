import { useState } from 'react'
import { toast } from 'sonner'
import { usePayReceivable } from '@/features/receivables/hooks/usePayReceivable'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import type { Receivable } from '@/features/receivables/types/receivable.types'

export function PayReceivableCell({ receivable }: { receivable: Receivable }) {
  const remaining = Number(receivable.amount) - Number(receivable.paidAmount ?? 0)
  const [amount, setAmount] = useState(remaining)
  const payReceivable = usePayReceivable()

  if (receivable.status === 'PAID' || receivable.status === 'CANCELLED') {
    return <span className="text-xs text-[var(--muted-foreground)]">—</span>
  }

  function handlePay() {
    payReceivable.mutate(
      { id: receivable.id, amount },
      {
        onSuccess: () => toast.success('Recebível baixado'),
        onError: () => toast.error('Não foi possível baixar o recebível'),
      },
    )
  }

  return (
    <div className="flex items-center gap-1.5">
      <Input
        type="number"
        step="0.01"
        min={0.01}
        value={amount}
        onChange={(e) => setAmount(Number(e.target.value))}
        className="h-8 w-24"
      />
      <Button size="sm" onClick={handlePay} disabled={payReceivable.isPending || amount <= 0}>
        Baixar
      </Button>
    </div>
  )
}

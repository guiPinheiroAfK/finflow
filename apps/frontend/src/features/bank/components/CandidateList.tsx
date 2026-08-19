import { toast } from 'sonner'
import { useReconcileTransaction } from '@/features/bank/hooks/useBank'
import { Button } from '@/shared/components/ui/button'
import type { BankTransaction, MatchCandidate } from '@/features/bank/types/bank.types'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

function pct(value: number) {
  return `${Math.round(value * 100)}%`
}

export function CandidateList({ transaction }: { transaction: BankTransaction }) {
  const reconcile = useReconcileTransaction()

  if (transaction.candidates.length === 0) {
    return <p className="text-xs text-[var(--muted-foreground)]">Nenhum candidato encontrado na janela de ±2 dias.</p>
  }

  function confirm(candidate: MatchCandidate) {
    const target =
      candidate.targetType === 'RECEIVABLE' ? { receivableId: candidate.targetId } : { payableId: candidate.targetId }
    reconcile.mutate(
      { id: transaction.id, target },
      {
        onSuccess: () => toast.success('Conciliação confirmada'),
        onError: () => toast.error('Não foi possível confirmar a conciliação'),
      },
    )
  }

  return (
    <div className="flex flex-col gap-2 rounded-md bg-[var(--muted)] p-3">
      <p className="text-xs font-medium text-[var(--muted-foreground)]">
        Candidatos (mesmo detalhamento que o algoritmo usou):
      </p>
      {transaction.candidates.map((candidate) => (
        <div
          key={candidate.targetId}
          className="flex items-center justify-between gap-3 rounded-md border border-[var(--border)] bg-[var(--background)] p-2 text-xs"
        >
          <div className="flex flex-col gap-0.5">
            <span className="font-medium">
              {candidate.targetType === 'RECEIVABLE' ? 'Recebível' : 'Pagável'} · {candidate.description ?? '—'}
            </span>
            <span className="text-[var(--muted-foreground)]">
              {formatMoney(candidate.amount)} · vence {candidate.dueDate}
            </span>
            <span className="text-[var(--muted-foreground)]">
              score {pct(candidate.score)} (valor {pct(candidate.valueScore)}, data {pct(candidate.dateScore)}, documento{' '}
              {pct(candidate.documentScore)})
            </span>
          </div>
          <Button size="sm" variant="outline" onClick={() => confirm(candidate)} disabled={reconcile.isPending}>
            Confirmar
          </Button>
        </div>
      ))}
    </div>
  )
}

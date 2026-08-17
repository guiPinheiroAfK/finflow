import { useState } from 'react'
import { CandidateList } from '@/features/bank/components/CandidateList'
import { Button } from '@/shared/components/ui/button'
import type { BankTransaction } from '@/features/bank/types/bank.types'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

export function TransactionRow({ transaction }: { transaction: BankTransaction }) {
  const [expanded, setExpanded] = useState(false)

  return (
    <div className="rounded-lg border border-[var(--border)]">
      <div className="flex items-center justify-between gap-3 p-3">
        <div className="flex flex-col gap-0.5">
          <span className="text-sm font-medium">{transaction.description}</span>
          <span className="text-xs text-[var(--muted-foreground)]">
            {transaction.date} · {transaction.type === 'CREDIT' ? 'Crédito' : 'Débito'}
          </span>
        </div>
        <div className="flex items-center gap-3">
          <span className={transaction.type === 'CREDIT' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}>
            {formatMoney(transaction.amount)}
          </span>
          {transaction.reconciled ? (
            <span className="rounded-full bg-green-500/15 px-2.5 py-0.5 text-xs font-medium text-green-600 dark:text-green-400">
              Conciliada ({transaction.matchedBy === 'AUTO' ? 'auto' : 'manual'})
            </span>
          ) : (
            <Button size="sm" variant="outline" onClick={() => setExpanded((e) => !e)}>
              {expanded ? 'Ocultar candidatos' : 'Revisar'}
            </Button>
          )}
        </div>
      </div>
      {expanded && !transaction.reconciled && (
        <div className="border-t border-[var(--border)] p-3">
          <CandidateList transaction={transaction} />
        </div>
      )}
    </div>
  )
}

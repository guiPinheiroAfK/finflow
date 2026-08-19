import { useRef, useState } from 'react'
import { toast } from 'sonner'
import { useAutoReconcile, useBankAccounts, useBankTransactions, useUploadBankTransactions } from '@/features/bank/hooks/useBank'
import { NewBankAccountForm } from '@/features/bank/components/NewBankAccountForm'
import { TransactionRow } from '@/features/bank/components/TransactionRow'
import { Button } from '@/shared/components/ui/button'

export function BankPage() {
  const { data: accounts } = useBankAccounts()
  const [bankAccountId, setBankAccountId] = useState('')
  const [filter, setFilter] = useState<'pending' | 'reconciled'>('pending')
  const fileInputRef = useRef<HTMLInputElement>(null)

  const { data, isLoading, isError } = useBankTransactions(filter === 'pending' ? false : true)
  const upload = useUploadBankTransactions()
  const autoReconcile = useAutoReconcile()

  function handleUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file || !bankAccountId) {
      toast.error('Selecione uma conta bancária antes de enviar o extrato')
      return
    }
    upload.mutate(
      { bankAccountId, file },
      {
        onSuccess: (transactions) => toast.success(`${transactions.length} transações importadas`),
        onError: () => toast.error('Não foi possível importar o extrato'),
      },
    )
    e.target.value = ''
  }

  function handleAutoReconcile() {
    if (!bankAccountId) {
      toast.error('Selecione uma conta bancária')
      return
    }
    autoReconcile.mutate(bankAccountId, {
      onSuccess: (result) => toast.success(`${result.autoReconciled} conciliadas automaticamente, ${result.pendingReview} em revisão`),
      onError: () => toast.error('Não foi possível rodar a conciliação automática'),
    })
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold">Extrato bancário</h1>

      <div className="flex flex-wrap items-end gap-4 rounded-lg border border-[var(--border)] p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-[var(--muted-foreground)]">Conta bancária</label>
          <select
            className="h-9 w-56 rounded-md border border-[var(--input)] bg-transparent px-3 text-sm"
            value={bankAccountId}
            onChange={(e) => setBankAccountId(e.target.value)}
          >
            <option value="">Selecione...</option>
            {accounts?.map((account) => (
              <option key={account.id} value={account.id}>
                {account.name} ({account.bankName})
              </option>
            ))}
          </select>
        </div>

        <NewBankAccountForm onCreated={setBankAccountId} />

        <Button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          disabled={upload.isPending || !bankAccountId}
        >
          {upload.isPending ? 'Enviando...' : 'Importar extrato (CSV)'}
        </Button>
        <input ref={fileInputRef} type="file" accept=".csv" className="hidden" onChange={handleUpload} />

        <Button variant="outline" onClick={handleAutoReconcile} disabled={autoReconcile.isPending || !bankAccountId}>
          {autoReconcile.isPending ? 'Rodando...' : 'Conciliar automaticamente'}
        </Button>
      </div>

      <div className="flex gap-2">
        <Button variant={filter === 'pending' ? 'default' : 'outline'} size="sm" onClick={() => setFilter('pending')}>
          Pendentes de revisão
        </Button>
        <Button variant={filter === 'reconciled' ? 'default' : 'outline'} size="sm" onClick={() => setFilter('reconciled')}>
          Conciliadas
        </Button>
      </div>

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar transações.</p>}

      <div className="flex flex-col gap-2">
        {data?.content.map((tx) => (
          <TransactionRow key={tx.id} transaction={tx} />
        ))}
        {data && data.content.length === 0 && (
          <p className="text-sm text-[var(--muted-foreground)]">Nenhuma transação nesta visualização.</p>
        )}
      </div>
    </div>
  )
}

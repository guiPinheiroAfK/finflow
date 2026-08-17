import { useState } from 'react'
import { toast } from 'sonner'
import { useCreateBankAccount } from '@/features/bank/hooks/useBank'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'

export function NewBankAccountForm({ onCreated }: { onCreated: (id: string) => void }) {
  const [name, setName] = useState('')
  const [bankName, setBankName] = useState('')
  const createAccount = useCreateBankAccount()

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    createAccount.mutate(
      { name, bankName, currency: 'BRL' },
      {
        onSuccess: (account) => {
          toast.success('Conta bancária criada')
          setName('')
          setBankName('')
          onCreated(account.id)
        },
        onError: () => toast.error('Não foi possível criar a conta'),
      },
    )
  }

  return (
    <form onSubmit={handleSubmit} className="flex items-end gap-2">
      <Input placeholder="Nome da conta" value={name} onChange={(e) => setName(e.target.value)} className="w-40" required />
      <Input placeholder="Banco" value={bankName} onChange={(e) => setBankName(e.target.value)} className="w-40" required />
      <Button type="submit" variant="outline" disabled={createAccount.isPending}>
        Nova conta
      </Button>
    </form>
  )
}

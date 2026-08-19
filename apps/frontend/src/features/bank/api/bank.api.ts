import { api } from '@/shared/lib/axios'
import type { BankAccount, BankTransaction, Page } from '@/features/bank/types/bank.types'

export async function getBankAccounts(): Promise<BankAccount[]> {
  const { data } = await api.get<BankAccount[]>('/api/v1/bank-accounts')
  return data
}

export async function createBankAccount(input: {
  name: string
  bankName: string
  agency?: string
  accountNumber?: string
  currency: string
}): Promise<BankAccount> {
  const { data } = await api.post<BankAccount>('/api/v1/bank-accounts', input)
  return data
}

export async function getBankTransactions(reconciled?: boolean): Promise<Page<BankTransaction>> {
  const { data } = await api.get<Page<BankTransaction>>('/api/v1/bank-transactions', {
    params: reconciled === undefined ? undefined : { reconciled },
  })
  return data
}

export async function uploadBankTransactions(bankAccountId: string, file: File): Promise<BankTransaction[]> {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post<BankTransaction[]>('/api/v1/bank-transactions/upload', formData, {
    params: { bankAccountId },
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}

export async function reconcileTransaction(
  id: string,
  target: { receivableId?: string; payableId?: string },
): Promise<BankTransaction> {
  const { data } = await api.post<BankTransaction>(`/api/v1/bank-transactions/${id}/reconcile`, target)
  return data
}

export async function autoReconcile(bankAccountId: string): Promise<{ autoReconciled: number; pendingReview: number }> {
  const { data } = await api.post(`/api/v1/bank-transactions/auto-reconcile`, null, {
    params: { bankAccountId },
  })
  return data
}

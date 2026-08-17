import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  autoReconcile,
  createBankAccount,
  getBankAccounts,
  getBankTransactions,
  reconcileTransaction,
  uploadBankTransactions,
} from '@/features/bank/api/bank.api'

export function useBankAccounts() {
  return useQuery({ queryKey: ['bank-accounts'], queryFn: getBankAccounts })
}

export function useCreateBankAccount() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: createBankAccount,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['bank-accounts'] }),
  })
}

export function useBankTransactions(reconciled?: boolean) {
  return useQuery({
    queryKey: ['bank-transactions', reconciled ?? 'all'],
    queryFn: () => getBankTransactions(reconciled),
  })
}

export function useUploadBankTransactions() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ bankAccountId, file }: { bankAccountId: string; file: File }) =>
      uploadBankTransactions(bankAccountId, file),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['bank-transactions'] }),
  })
}

export function useReconcileTransaction() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, target }: { id: string; target: { receivableId?: string; payableId?: string } }) =>
      reconcileTransaction(id, target),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bank-transactions'] })
      queryClient.invalidateQueries({ queryKey: ['receivables'] })
      queryClient.invalidateQueries({ queryKey: ['payables'] })
    },
  })
}

export function useAutoReconcile() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: autoReconcile,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bank-transactions'] })
      queryClient.invalidateQueries({ queryKey: ['receivables'] })
      queryClient.invalidateQueries({ queryKey: ['payables'] })
    },
  })
}

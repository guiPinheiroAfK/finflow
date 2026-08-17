export type Currency = 'BRL' | 'USD' | 'EUR' | 'ARS'
export type TransactionType = 'CREDIT' | 'DEBIT'
export type MatchedBy = 'AUTO' | 'MANUAL' | null

export interface BankAccount {
  id: string
  name: string
  bankName: string
  agency: string | null
  accountNumber: string | null
  currency: Currency
  active: boolean
  createdAt: string
}

export interface MatchCandidate {
  targetType: 'RECEIVABLE' | 'PAYABLE'
  targetId: string
  description: string | null
  amount: string
  dueDate: string
  score: number
  valueScore: number
  dateScore: number
  documentScore: number
}

export interface BankTransaction {
  id: string
  bankAccountId: string
  date: string
  description: string
  amount: string
  type: TransactionType
  reconciled: boolean
  receivableId: string | null
  payableId: string | null
  matchedBy: MatchedBy
  matchedScore: string | null
  matchMargin: string | null
  candidates: MatchCandidate[]
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

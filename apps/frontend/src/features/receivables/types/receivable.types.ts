export type ReceivableStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'OVERDUE' | 'CANCELLED'

export interface Receivable {
  id: string
  orderId: string
  orderNumber: string
  customerId: string
  customerName: string
  description: string | null
  amount: string
  dueDate: string
  paidAt: string | null
  paidAmount: string | null
  status: ReceivableStatus
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

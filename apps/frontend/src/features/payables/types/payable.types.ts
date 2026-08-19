export type PayableStatus = 'PENDING' | 'PAID' | 'CANCELLED'
export type Currency = 'BRL' | 'USD' | 'EUR' | 'ARS'

export interface Payable {
  id: string
  orderId: string | null
  orderNumber: string | null
  supplierId: string
  supplierName: string
  description: string | null
  amount: string
  currency: Currency
  exchangeRate: string | null
  amountBrl: string
  dueDate: string
  paidAt: string | null
  status: PayableStatus
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

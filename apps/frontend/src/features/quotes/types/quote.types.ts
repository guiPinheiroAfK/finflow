export type QuoteStatus = 'DRAFT' | 'SENT' | 'APPROVED' | 'REJECTED' | 'EXPIRED'

export interface QuoteItem {
  id: string
  productId: string
  productName: string
  description: string | null
  quantity: number
  unitCost: string
  unitSale: string
  travelDate: string | null
  passengerNames: string[] | null
}

export interface Quote {
  id: string
  quoteNumber: string
  customerId: string
  customerName: string
  sellerId: string
  sellerName: string
  status: QuoteStatus
  validUntil: string | null
  notes: string | null
  totalCost: string
  totalSale: string
  margin: string
  items: QuoteItem[]
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

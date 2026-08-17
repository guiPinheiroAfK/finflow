export type OrderStatus = 'CONFIRMED' | 'ISSUED' | 'CANCELLED' | 'COMPLETED'
export type PaymentMethod = 'DINHEIRO' | 'CARTAO_CREDITO' | 'CARTAO_DEBITO' | 'PIX' | 'BOLETO' | 'TRANSFERENCIA'
export type Currency = 'BRL' | 'USD' | 'EUR' | 'ARS'

export interface OrderItem {
  id: string
  productId: string
  productName: string
  description: string | null
  quantity: number
  unitCost: string
  unitCostCurrency: Currency
  unitCostExchangeRate: string | null
  unitCostBrl: string
  unitSale: string
  travelDate: string | null
  passengerNames: string[] | null
}

export interface Order {
  id: string
  orderNumber: string
  quoteId: string | null
  customerId: string
  customerName: string
  sellerId: string
  sellerName: string
  status: OrderStatus
  paymentMethod: PaymentMethod
  installments: number
  totalSale: string
  totalCost: string
  grossMargin: string
  commissionPct: string
  commissionValue: string
  confirmedAt: string
  items: OrderItem[]
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

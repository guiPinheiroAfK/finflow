export type SupplierCategory = 'HOTEL' | 'AEREA' | 'TRANSFER' | 'PASSEIO' | 'SEGURO' | 'OUTRO'
export type Currency = 'BRL' | 'USD' | 'EUR' | 'ARS'

export interface Supplier {
  id: string
  name: string
  category: SupplierCategory
  document: string | null
  contactName: string | null
  email: string | null
  paymentTermDays: number
  currency: Currency
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

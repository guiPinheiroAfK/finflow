export type ProductCategory = 'PACOTE' | 'PASSAGEM' | 'HOSPEDAGEM' | 'TRANSFER' | 'SEGURO' | 'INGRESSO'
export type Currency = 'BRL' | 'USD' | 'EUR' | 'ARS'

export interface Product {
  id: string
  name: string
  category: ProductCategory
  supplierId: string
  supplierName: string
  costPrice: string
  currency: Currency
  salePrice: string
  markupPct: string | null
  active: boolean
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

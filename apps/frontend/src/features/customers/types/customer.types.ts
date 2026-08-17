export type CustomerType = 'PESSOA_FISICA' | 'PESSOA_JURIDICA'

export interface Address {
  street: string | null
  number: string | null
  city: string | null
  state: string | null
  zip: string | null
}

export interface Customer {
  id: string
  type: CustomerType
  name: string
  document: string
  email: string | null
  phone: string | null
  address: Address | null
  tags: string[]
  createdAt: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

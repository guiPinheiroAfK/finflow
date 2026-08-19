import { api } from '@/shared/lib/axios'
import type { CustomerInput } from '@/features/customers/schemas/customer.schema'
import type { Customer, Page } from '@/features/customers/types/customer.types'

export async function getCustomers(nameFilter?: string): Promise<Page<Customer>> {
  const { data } = await api.get<Page<Customer>>('/api/v1/customers', {
    params: nameFilter ? { name: nameFilter } : undefined,
  })
  return data
}

export async function getCustomer(id: string): Promise<Customer> {
  const { data } = await api.get<Customer>(`/api/v1/customers/${id}`)
  return data
}

export async function createCustomer(input: CustomerInput): Promise<Customer> {
  const { data } = await api.post<Customer>('/api/v1/customers', input)
  return data
}

export async function updateCustomer(id: string, input: CustomerInput): Promise<Customer> {
  const { data } = await api.put<Customer>(`/api/v1/customers/${id}`, input)
  return data
}

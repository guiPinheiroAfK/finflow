import { api } from '@/shared/lib/axios'
import type { SupplierInput } from '@/features/suppliers/schemas/supplier.schema'
import type { Page, Supplier } from '@/features/suppliers/types/supplier.types'

export async function getSuppliers(nameFilter?: string): Promise<Page<Supplier>> {
  const { data } = await api.get<Page<Supplier>>('/api/v1/suppliers', {
    params: nameFilter ? { name: nameFilter } : undefined,
  })
  return data
}

export async function getSupplier(id: string): Promise<Supplier> {
  const { data } = await api.get<Supplier>(`/api/v1/suppliers/${id}`)
  return data
}

export async function createSupplier(input: SupplierInput): Promise<Supplier> {
  const { data } = await api.post<Supplier>('/api/v1/suppliers', input)
  return data
}

export async function updateSupplier(id: string, input: SupplierInput): Promise<Supplier> {
  const { data } = await api.put<Supplier>(`/api/v1/suppliers/${id}`, input)
  return data
}

import { api } from '@/shared/lib/axios'
import type { ProductInput } from '@/features/products/schemas/product.schema'
import type { Page, Product } from '@/features/products/types/product.types'

export async function getProducts(nameFilter?: string): Promise<Page<Product>> {
  const { data } = await api.get<Page<Product>>('/api/v1/products', {
    params: nameFilter ? { name: nameFilter } : undefined,
  })
  return data
}

export async function getProduct(id: string): Promise<Product> {
  const { data } = await api.get<Product>(`/api/v1/products/${id}`)
  return data
}

export async function createProduct(input: ProductInput): Promise<Product> {
  const { data } = await api.post<Product>('/api/v1/products', input)
  return data
}

export async function updateProduct(id: string, input: ProductInput): Promise<Product> {
  const { data } = await api.put<Product>(`/api/v1/products/${id}`, input)
  return data
}

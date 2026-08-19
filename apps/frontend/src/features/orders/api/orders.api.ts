import { api } from '@/shared/lib/axios'
import type { Order, Page } from '@/features/orders/types/order.types'

export async function getOrders(): Promise<Page<Order>> {
  const { data } = await api.get<Page<Order>>('/api/v1/orders')
  return data
}

export async function getOrder(id: string): Promise<Order> {
  const { data } = await api.get<Order>(`/api/v1/orders/${id}`)
  return data
}

export async function issueOrder(id: string): Promise<Order> {
  const { data } = await api.post<Order>(`/api/v1/orders/${id}/issue`)
  return data
}

export async function cancelOrder(id: string): Promise<Order> {
  const { data } = await api.post<Order>(`/api/v1/orders/${id}/cancel`)
  return data
}

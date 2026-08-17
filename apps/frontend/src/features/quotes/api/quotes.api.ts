import { api } from '@/shared/lib/axios'
import type { ApproveQuoteInput } from '@/features/quotes/schemas/approve.schema'
import type { QuoteInput } from '@/features/quotes/schemas/quote.schema'
import type { Page, Quote, QuoteStatus } from '@/features/quotes/types/quote.types'
import type { Order } from '@/features/orders/types/order.types'

export async function getQuotes(status?: QuoteStatus): Promise<Page<Quote>> {
  const { data } = await api.get<Page<Quote>>('/api/v1/quotes', {
    params: status ? { status } : undefined,
  })
  return data
}

export async function getQuote(id: string): Promise<Quote> {
  const { data } = await api.get<Quote>(`/api/v1/quotes/${id}`)
  return data
}

export async function createQuote(input: QuoteInput): Promise<Quote> {
  const { data } = await api.post<Quote>('/api/v1/quotes', input)
  return data
}

export async function updateQuote(id: string, input: QuoteInput): Promise<Quote> {
  const { data } = await api.put<Quote>(`/api/v1/quotes/${id}`, input)
  return data
}

export async function sendQuote(id: string): Promise<Quote> {
  const { data } = await api.post<Quote>(`/api/v1/quotes/${id}/send`)
  return data
}

export async function approveQuote(id: string, input: ApproveQuoteInput): Promise<Order> {
  const { data } = await api.post<Order>(`/api/v1/quotes/${id}/approve`, input)
  return data
}

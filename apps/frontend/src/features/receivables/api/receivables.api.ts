import { api } from '@/shared/lib/axios'
import type { Page, Receivable, ReceivableStatus } from '@/features/receivables/types/receivable.types'

export async function getReceivables(status?: ReceivableStatus): Promise<Page<Receivable>> {
  const { data } = await api.get<Page<Receivable>>('/api/v1/receivables', {
    params: status ? { status } : undefined,
  })
  return data
}

export async function payReceivable(id: string, amount: number): Promise<Receivable> {
  const { data } = await api.post<Receivable>(`/api/v1/receivables/${id}/pay`, { amount })
  return data
}

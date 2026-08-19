import { api } from '@/shared/lib/axios'
import type { Page, Payable, PayableStatus } from '@/features/payables/types/payable.types'

export async function getPayables(status?: PayableStatus): Promise<Page<Payable>> {
  const { data } = await api.get<Page<Payable>>('/api/v1/payables', {
    params: status ? { status } : undefined,
  })
  return data
}

export async function payPayable(id: string): Promise<Payable> {
  const { data } = await api.post<Payable>(`/api/v1/payables/${id}/pay`)
  return data
}

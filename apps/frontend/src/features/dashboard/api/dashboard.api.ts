import { api } from '@/shared/lib/axios'
import type { DashboardSummary } from '@/features/dashboard/types/dashboard.types'

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const { data } = await api.get<DashboardSummary>('/api/v1/dashboard/summary')
  return data
}

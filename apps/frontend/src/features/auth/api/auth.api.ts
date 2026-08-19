import { api } from '@/shared/lib/axios'
import type { LoginInput } from '@/features/auth/schemas/auth.schema'
import type { TokenResponse, User } from '@/features/auth/types/auth.types'

export async function login(input: LoginInput): Promise<TokenResponse> {
  const { data } = await api.post<TokenResponse>('/api/v1/auth/login', input)
  return data
}

export async function getCurrentUser(): Promise<User> {
  const { data } = await api.get<User>('/api/v1/auth/me')
  return data
}

export async function logout(refreshToken: string): Promise<void> {
  await api.post('/api/v1/auth/logout', { refreshToken })
}

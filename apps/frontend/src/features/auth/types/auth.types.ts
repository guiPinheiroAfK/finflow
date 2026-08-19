export type Role = 'ADMIN' | 'FINANCE' | 'SELLER'

export interface User {
  id: string
  name: string
  email: string
  role: Role
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  expiresInSeconds: number
}

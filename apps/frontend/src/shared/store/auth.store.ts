import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { TokenResponse, User } from '@/features/auth/types/auth.types'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: User | null
  setSession: (tokens: TokenResponse, user: User) => void
  setAccessToken: (token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      setSession: (tokens, user) =>
        set({ accessToken: tokens.accessToken, refreshToken: tokens.refreshToken, user }),
      setAccessToken: (token) => set({ accessToken: token }),
      logout: () => set({ accessToken: null, refreshToken: null, user: null }),
    }),
    {
      name: 'finflow-auth',
      // accessToken de propósito fora do storage: vive só em memória (vida
      // curta, ADR-0005) -- persistir só o necessário para re-obter sessão.
      partialize: (s) => ({ refreshToken: s.refreshToken, user: s.user }),
    },
  ),
)

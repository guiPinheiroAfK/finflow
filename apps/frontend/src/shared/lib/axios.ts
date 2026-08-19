import axios, { type InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/shared/store/auth.store'
import type { TokenResponse } from '@/features/auth/types/auth.types'

const baseURL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export const api = axios.create({ baseURL })

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Dedupe: o backend rotaciona o refresh token a cada uso (ADR-0005) -- duas
// requisições 401 concorrentes não podem disparar dois refreshes, senão a
// segunda usaria um jti já revogado pela primeira.
let refreshPromise: Promise<string> | null = null

async function refreshAccessToken(): Promise<string> {
  const refreshToken = useAuthStore.getState().refreshToken
  if (!refreshToken) {
    throw new Error('Sem refresh token disponível')
  }

  const { data } = await axios.post<TokenResponse>(`${baseURL}/api/v1/auth/refresh`, { refreshToken })
  useAuthStore.getState().setAccessToken(data.accessToken)
  useAuthStore.setState({ refreshToken: data.refreshToken })
  return data.accessToken
}

interface RetryableConfig extends InternalAxiosRequestConfig {
  _retry?: boolean
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config as RetryableConfig | undefined

    if (error.response?.status === 401 && original && !original._retry) {
      original._retry = true
      try {
        refreshPromise ??= refreshAccessToken().finally(() => {
          refreshPromise = null
        })
        const newToken = await refreshPromise
        original.headers.Authorization = `Bearer ${newToken}`
        return api(original)
      } catch (refreshError) {
        useAuthStore.getState().logout()
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

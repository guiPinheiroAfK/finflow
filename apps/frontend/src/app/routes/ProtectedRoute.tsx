import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/shared/store/auth.store'

export function ProtectedRoute() {
  // refreshToken persiste entre sessões; accessToken vive só em memória
  // (ADR-0005) -- refreshToken presente já basta para tentar a rota
  // protegida, o interceptor do axios renova o access token sob demanda.
  const refreshToken = useAuthStore((s) => s.refreshToken)

  if (!refreshToken) {
    return <Navigate to="/login" replace />
  }

  return <Outlet />
}

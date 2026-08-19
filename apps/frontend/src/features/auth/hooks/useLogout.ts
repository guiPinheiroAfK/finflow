import { useMutation } from '@tanstack/react-query'
import { logout as logoutApi } from '@/features/auth/api/auth.api'
import { useAuthStore } from '@/shared/store/auth.store'

export function useLogout() {
  const refreshToken = useAuthStore((s) => s.refreshToken)
  const clearSession = useAuthStore((s) => s.logout)

  return useMutation({
    mutationFn: async () => {
      if (refreshToken) {
        await logoutApi(refreshToken)
      }
    },
    onSettled: () => clearSession(),
  })
}

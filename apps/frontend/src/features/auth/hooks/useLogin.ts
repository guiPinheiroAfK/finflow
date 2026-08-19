import { useMutation } from '@tanstack/react-query'
import { getCurrentUser, login } from '@/features/auth/api/auth.api'
import type { LoginInput } from '@/features/auth/schemas/auth.schema'
import { useAuthStore } from '@/shared/store/auth.store'

export function useLogin() {
  const setSession = useAuthStore((s) => s.setSession)
  const setAccessToken = useAuthStore((s) => s.setAccessToken)

  return useMutation({
    mutationFn: async (input: LoginInput) => {
      const tokens = await login(input)
      setAccessToken(tokens.accessToken) // necessário para o interceptor autenticar a chamada seguinte
      const user = await getCurrentUser()
      setSession(tokens, user)
      return user
    },
  })
}

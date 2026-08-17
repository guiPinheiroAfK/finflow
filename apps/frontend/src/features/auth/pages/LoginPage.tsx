import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'
import { loginSchema, type LoginInput } from '@/features/auth/schemas/auth.schema'
import { useLogin } from '@/features/auth/hooks/useLogin'
import { Button } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'
import { Label } from '@/shared/components/ui/label'
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card'

export function LoginPage() {
  const navigate = useNavigate()
  const loginMutation = useLogin()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginInput>({ resolver: zodResolver(loginSchema) })

  function onSubmit(input: LoginInput) {
    loginMutation.mutate(input, {
      onSuccess: () => navigate('/'),
      onError: () => toast.error('E-mail ou senha inválidos'),
    })
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[var(--muted)] px-4">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle>finflow</CardTitle>
          <p className="text-sm text-[var(--muted-foreground)]">Entre com sua conta</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="email">E-mail</Label>
              <Input id="email" type="email" autoComplete="email" {...register('email')} />
              {errors.email && <p className="text-xs text-[var(--destructive)]">{errors.email.message}</p>}
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="password">Senha</Label>
              <Input id="password" type="password" autoComplete="current-password" {...register('password')} />
              {errors.password && <p className="text-xs text-[var(--destructive)]">{errors.password.message}</p>}
            </div>
            <Button type="submit" disabled={loginMutation.isPending} className="mt-2">
              {loginMutation.isPending ? 'Entrando...' : 'Entrar'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

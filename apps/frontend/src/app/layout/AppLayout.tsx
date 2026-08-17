import { NavLink, Outlet } from 'react-router-dom'
import { cn } from '@/shared/lib/utils'
import { useAuthStore } from '@/shared/store/auth.store'
import { useLogout } from '@/features/auth/hooks/useLogout'
import { Button } from '@/shared/components/ui/button'

const NAV_SECTIONS = [
  {
    label: 'Comercial',
    items: [
      { to: '/', label: 'Dashboard' },
      { to: '/quotes', label: 'Orçamentos' },
      { to: '/orders', label: 'Vendas' },
    ],
  },
  {
    label: 'Financeiro',
    items: [
      { to: '/receivables', label: 'Contas a receber' },
      { to: '/payables', label: 'Contas a pagar' },
      { to: '/bank', label: 'Extrato bancário' },
      { to: '/cash-flow', label: 'Fluxo de caixa' },
    ],
  },
  {
    label: 'Cadastros',
    items: [
      { to: '/customers', label: 'Clientes' },
      { to: '/suppliers', label: 'Fornecedores' },
      { to: '/products', label: 'Produtos' },
    ],
  },
]

export function AppLayout() {
  const user = useAuthStore((s) => s.user)
  const logout = useLogout()

  return (
    <div className="flex min-h-screen">
      <aside className="flex w-60 flex-col border-r border-[var(--border)] bg-[var(--card)] p-4">
        <div className="mb-6 px-2 text-lg font-semibold">finflow</div>
        <nav className="flex flex-1 flex-col gap-5">
          {NAV_SECTIONS.map((section) => (
            <div key={section.label}>
              <p className="mb-1 px-2 text-xs font-medium uppercase text-[var(--muted-foreground)]">
                {section.label}
              </p>
              <div className="flex flex-col gap-0.5">
                {section.items.map((item) => (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    end={item.to === '/'}
                    className={({ isActive }) =>
                      cn(
                        'rounded-md px-2 py-1.5 text-sm hover:bg-[var(--accent)]',
                        isActive && 'bg-[var(--accent)] font-medium',
                      )
                    }
                  >
                    {item.label}
                  </NavLink>
                ))}
              </div>
            </div>
          ))}
        </nav>
        <div className="border-t border-[var(--border)] pt-3">
          <p className="px-2 text-sm font-medium">{user?.name}</p>
          <p className="px-2 text-xs text-[var(--muted-foreground)]">{user?.email}</p>
          <Button variant="ghost" size="sm" className="mt-2 w-full justify-start" onClick={() => logout.mutate()}>
            Sair
          </Button>
        </div>
      </aside>
      <main className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </main>
    </div>
  )
}

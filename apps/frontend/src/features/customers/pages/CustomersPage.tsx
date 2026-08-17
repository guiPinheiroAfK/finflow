import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useCustomers } from '@/features/customers/hooks/useCustomers'
import { CustomerList } from '@/features/customers/components/CustomerList'
import { buttonVariants } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'

export function CustomersPage() {
  const [nameFilter, setNameFilter] = useState('')
  const { data, isLoading, isError } = useCustomers(nameFilter)

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Clientes</h1>
        <Link to="/customers/new" className={buttonVariants()}>
          Novo cliente
        </Link>
      </div>

      <Input
        placeholder="Buscar por nome..."
        value={nameFilter}
        onChange={(e) => setNameFilter(e.target.value)}
        className="max-w-sm"
      />

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar clientes.</p>}
      {data && <CustomerList customers={data.content} />}
    </div>
  )
}

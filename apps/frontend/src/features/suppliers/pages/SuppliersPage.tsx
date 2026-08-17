import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useSuppliers } from '@/features/suppliers/hooks/useSuppliers'
import { SupplierList } from '@/features/suppliers/components/SupplierList'
import { buttonVariants } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'

export function SuppliersPage() {
  const [nameFilter, setNameFilter] = useState('')
  const { data, isLoading, isError } = useSuppliers(nameFilter)

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Fornecedores</h1>
        <Link to="/suppliers/new" className={buttonVariants()}>
          Novo fornecedor
        </Link>
      </div>

      <Input
        placeholder="Buscar por nome..."
        value={nameFilter}
        onChange={(e) => setNameFilter(e.target.value)}
        className="max-w-sm"
      />

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar fornecedores.</p>}
      {data && <SupplierList suppliers={data.content} />}
    </div>
  )
}

import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useProducts } from '@/features/products/hooks/useProducts'
import { ProductList } from '@/features/products/components/ProductList'
import { buttonVariants } from '@/shared/components/ui/button'
import { Input } from '@/shared/components/ui/input'

export function ProductsPage() {
  const [nameFilter, setNameFilter] = useState('')
  const { data, isLoading, isError } = useProducts(nameFilter)

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Produtos</h1>
        <Link to="/products/new" className={buttonVariants()}>
          Novo produto
        </Link>
      </div>

      <Input
        placeholder="Buscar por nome..."
        value={nameFilter}
        onChange={(e) => setNameFilter(e.target.value)}
        className="max-w-sm"
      />

      {isLoading && <p className="text-sm text-[var(--muted-foreground)]">Carregando...</p>}
      {isError && <p className="text-sm text-[var(--destructive)]">Erro ao carregar produtos.</p>}
      {data && <ProductList products={data.content} />}
    </div>
  )
}

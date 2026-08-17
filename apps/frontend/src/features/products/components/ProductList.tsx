import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import type { Product } from '@/features/products/types/product.types'

const CATEGORY_LABELS: Record<Product['category'], string> = {
  PACOTE: 'Pacote',
  PASSAGEM: 'Passagem',
  HOSPEDAGEM: 'Hospedagem',
  TRANSFER: 'Transfer',
  SEGURO: 'Seguro',
  INGRESSO: 'Ingresso',
}

function formatMoney(value: string, currency: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency }).format(Number(value))
}

const columns: ColumnDef<Product>[] = [
  { accessorKey: 'name', header: 'Nome' },
  { accessorKey: 'category', header: 'Categoria', cell: ({ getValue }) => CATEGORY_LABELS[getValue() as Product['category']] },
  { accessorKey: 'supplierName', header: 'Fornecedor' },
  {
    id: 'costPrice',
    header: 'Custo',
    cell: ({ row }) => formatMoney(row.original.costPrice, row.original.currency),
  },
  {
    id: 'salePrice',
    header: 'Venda',
    cell: ({ row }) => formatMoney(row.original.salePrice, row.original.currency),
  },
  {
    id: 'markupPct',
    header: 'Markup',
    cell: ({ row }) =>
      row.original.markupPct == null ? '—' : `${(Number(row.original.markupPct) * 100).toFixed(1)}%`,
  },
  {
    accessorKey: 'active',
    header: 'Status',
    cell: ({ getValue }) => (getValue() ? 'Ativo' : 'Inativo'),
  },
]

export function ProductList({ products }: { products: Product[] }) {
  const table = useReactTable({ data: products, columns, getCoreRowModel: getCoreRowModel() })

  return (
    <div className="overflow-x-auto rounded-lg border border-[var(--border)]">
      <table className="w-full text-sm">
        <thead className="bg-[var(--muted)] text-left">
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th key={header.id} className="px-4 py-2 font-medium text-[var(--muted-foreground)]">
                  {flexRender(header.column.columnDef.header, header.getContext())}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row) => (
            <tr key={row.id} className="border-t border-[var(--border)]">
              {row.getVisibleCells().map((cell) => (
                <td key={cell.id} className="px-4 py-2">
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
          {products.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhum produto encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

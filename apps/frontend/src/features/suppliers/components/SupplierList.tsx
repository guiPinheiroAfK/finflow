import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import type { Supplier } from '@/features/suppliers/types/supplier.types'

const CATEGORY_LABELS: Record<Supplier['category'], string> = {
  HOTEL: 'Hotel',
  AEREA: 'Aérea',
  TRANSFER: 'Transfer',
  PASSEIO: 'Passeio',
  SEGURO: 'Seguro',
  OUTRO: 'Outro',
}

const columns: ColumnDef<Supplier>[] = [
  { accessorKey: 'name', header: 'Nome' },
  { accessorKey: 'category', header: 'Categoria', cell: ({ getValue }) => CATEGORY_LABELS[getValue() as Supplier['category']] },
  { accessorKey: 'contactName', header: 'Contato' },
  { accessorKey: 'email', header: 'E-mail' },
  { accessorKey: 'paymentTermDays', header: 'Prazo (dias)' },
  { accessorKey: 'currency', header: 'Moeda' },
]

export function SupplierList({ suppliers }: { suppliers: Supplier[] }) {
  const table = useReactTable({ data: suppliers, columns, getCoreRowModel: getCoreRowModel() })

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
          {suppliers.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhum fornecedor encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { Link } from 'react-router-dom'
import type { Quote } from '@/features/quotes/types/quote.types'
import { QuoteStatusBadge } from '@/features/quotes/components/QuoteStatusBadge'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

const columns: ColumnDef<Quote>[] = [
  {
    accessorKey: 'quoteNumber',
    header: 'Número',
    cell: ({ row }) => (
      <Link to={`/quotes/${row.original.id}`} className="font-medium text-[var(--primary)] hover:underline">
        {row.original.quoteNumber}
      </Link>
    ),
  },
  { accessorKey: 'customerName', header: 'Cliente' },
  { accessorKey: 'sellerName', header: 'Vendedor' },
  { accessorKey: 'status', header: 'Status', cell: ({ getValue }) => <QuoteStatusBadge status={getValue() as Quote['status']} /> },
  { id: 'totalSale', header: 'Total', cell: ({ row }) => formatMoney(row.original.totalSale) },
  { id: 'margin', header: 'Margem', cell: ({ row }) => formatMoney(row.original.margin) },
]

export function QuoteList({ quotes }: { quotes: Quote[] }) {
  const table = useReactTable({ data: quotes, columns, getCoreRowModel: getCoreRowModel() })

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
          {quotes.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhum orçamento encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

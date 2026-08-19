import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import type { Receivable } from '@/features/receivables/types/receivable.types'
import { ReceivableStatusBadge } from '@/features/receivables/components/ReceivableStatusBadge'
import { PayReceivableCell } from '@/features/receivables/components/PayReceivableCell'

function formatMoney(value: string | number) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

const columns: ColumnDef<Receivable>[] = [
  { accessorKey: 'orderNumber', header: 'Venda' },
  { accessorKey: 'customerName', header: 'Cliente' },
  { accessorKey: 'description', header: 'Descrição' },
  { id: 'amount', header: 'Valor', cell: ({ row }) => formatMoney(row.original.amount) },
  { accessorKey: 'dueDate', header: 'Vencimento' },
  { accessorKey: 'status', header: 'Status', cell: ({ getValue }) => <ReceivableStatusBadge status={getValue() as Receivable['status']} /> },
  { id: 'pay', header: 'Baixa', cell: ({ row }) => <PayReceivableCell receivable={row.original} /> },
]

export function ReceivableList({ receivables }: { receivables: Receivable[] }) {
  const table = useReactTable({ data: receivables, columns, getCoreRowModel: getCoreRowModel() })

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
          {receivables.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhum recebível encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

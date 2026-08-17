import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { toast } from 'sonner'
import type { Payable } from '@/features/payables/types/payable.types'
import { PayableStatusBadge } from '@/features/payables/components/PayableStatusBadge'
import { usePayPayable } from '@/features/payables/hooks/usePayPayable'
import { Button } from '@/shared/components/ui/button'

function formatMoney(value: string, currency: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency }).format(Number(value))
}

function PayCell({ payable }: { payable: Payable }) {
  const payPayable = usePayPayable()
  if (payable.status !== 'PENDING') return <span className="text-xs text-[var(--muted-foreground)]">—</span>

  return (
    <Button
      size="sm"
      disabled={payPayable.isPending}
      onClick={() =>
        payPayable.mutate(payable.id, {
          onSuccess: () => toast.success('Pagável baixado'),
          onError: () => toast.error('Não foi possível baixar o pagável'),
        })
      }
    >
      Pagar
    </Button>
  )
}

const columns: ColumnDef<Payable>[] = [
  { accessorKey: 'supplierName', header: 'Fornecedor' },
  { accessorKey: 'orderNumber', header: 'Venda', cell: ({ getValue }) => (getValue() as string | null) ?? '—' },
  { accessorKey: 'description', header: 'Descrição' },
  { id: 'amount', header: 'Valor', cell: ({ row }) => formatMoney(row.original.amount, row.original.currency) },
  { id: 'amountBrl', header: 'Valor BRL', cell: ({ row }) => formatMoney(row.original.amountBrl, 'BRL') },
  { accessorKey: 'dueDate', header: 'Vencimento' },
  { accessorKey: 'status', header: 'Status', cell: ({ getValue }) => <PayableStatusBadge status={getValue() as Payable['status']} /> },
  { id: 'pay', header: 'Baixa', cell: ({ row }) => <PayCell payable={row.original} /> },
]

export function PayableList({ payables }: { payables: Payable[] }) {
  const table = useReactTable({ data: payables, columns, getCoreRowModel: getCoreRowModel() })

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
          {payables.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhum pagável encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

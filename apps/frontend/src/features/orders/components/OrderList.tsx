import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { Link } from 'react-router-dom'
import type { Order } from '@/features/orders/types/order.types'
import { OrderStatusBadge } from '@/features/orders/components/OrderStatusBadge'

function formatMoney(value: string) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(value))
}

const columns: ColumnDef<Order>[] = [
  {
    accessorKey: 'orderNumber',
    header: 'Número',
    cell: ({ row }) => (
      <Link to={`/orders/${row.original.id}`} className="font-medium text-[var(--primary)] hover:underline">
        {row.original.orderNumber}
      </Link>
    ),
  },
  { accessorKey: 'customerName', header: 'Cliente' },
  { accessorKey: 'status', header: 'Status', cell: ({ getValue }) => <OrderStatusBadge status={getValue() as Order['status']} /> },
  { id: 'totalSale', header: 'Total', cell: ({ row }) => formatMoney(row.original.totalSale) },
  { id: 'grossMargin', header: 'Margem', cell: ({ row }) => formatMoney(row.original.grossMargin) },
  { accessorKey: 'installments', header: 'Parcelas' },
]

export function OrderList({ orders }: { orders: Order[] }) {
  const table = useReactTable({ data: orders, columns, getCoreRowModel: getCoreRowModel() })

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
          {orders.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-[var(--muted-foreground)]">
                Nenhuma venda encontrada.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  )
}

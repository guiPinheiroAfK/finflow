import { createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '@/app/layout/AppLayout'
import { ProtectedRoute } from '@/app/routes/ProtectedRoute'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { CustomersPage } from '@/features/customers/pages/CustomersPage'
import { NewCustomerPage } from '@/features/customers/pages/NewCustomerPage'
import { SuppliersPage } from '@/features/suppliers/pages/SuppliersPage'
import { NewSupplierPage } from '@/features/suppliers/pages/NewSupplierPage'
import { ProductsPage } from '@/features/products/pages/ProductsPage'
import { NewProductPage } from '@/features/products/pages/NewProductPage'
import { QuotesPage } from '@/features/quotes/pages/QuotesPage'
import { NewQuotePage } from '@/features/quotes/pages/NewQuotePage'
import { QuoteDetailPage } from '@/features/quotes/pages/QuoteDetailPage'
import { OrdersPage } from '@/features/orders/pages/OrdersPage'
import { OrderDetailPage } from '@/features/orders/pages/OrderDetailPage'
import { PlaceholderPage } from '@/shared/components/PlaceholderPage'

export const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <PlaceholderPage title="Dashboard" /> },
          { path: '/quotes', element: <QuotesPage /> },
          { path: '/quotes/new', element: <NewQuotePage /> },
          { path: '/quotes/:id', element: <QuoteDetailPage /> },
          { path: '/orders', element: <OrdersPage /> },
          { path: '/orders/:id', element: <OrderDetailPage /> },
          { path: '/receivables', element: <PlaceholderPage title="Contas a receber" /> },
          { path: '/payables', element: <PlaceholderPage title="Contas a pagar" /> },
          { path: '/bank', element: <PlaceholderPage title="Extrato bancário" /> },
          { path: '/cash-flow', element: <PlaceholderPage title="Fluxo de caixa" /> },
          { path: '/customers', element: <CustomersPage /> },
          { path: '/customers/new', element: <NewCustomerPage /> },
          { path: '/suppliers', element: <SuppliersPage /> },
          { path: '/suppliers/new', element: <NewSupplierPage /> },
          { path: '/products', element: <ProductsPage /> },
          { path: '/products/new', element: <NewProductPage /> },
        ],
      },
    ],
  },
])

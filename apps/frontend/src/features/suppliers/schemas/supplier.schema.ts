import { z } from 'zod'

export const supplierSchema = z.object({
  name: z.string().min(1, 'Nome obrigatório'),
  category: z.enum(['HOTEL', 'AEREA', 'TRANSFER', 'PASSEIO', 'SEGURO', 'OUTRO']),
  document: z.string().optional(),
  contactName: z.string().optional(),
  email: z.string().email('E-mail inválido').optional().or(z.literal('')),
  paymentTermDays: z.number().int().min(0, 'Não pode ser negativo'),
  currency: z.enum(['BRL', 'USD', 'EUR', 'ARS']),
})

export type SupplierInput = z.infer<typeof supplierSchema>

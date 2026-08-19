import { z } from 'zod'

export const productSchema = z.object({
  name: z.string().min(1, 'Nome obrigatório'),
  category: z.enum(['PACOTE', 'PASSAGEM', 'HOSPEDAGEM', 'TRANSFER', 'SEGURO', 'INGRESSO']),
  supplierId: z.string().min(1, 'Selecione um fornecedor'),
  costPrice: z.number().min(0, 'Não pode ser negativo'),
  currency: z.enum(['BRL', 'USD', 'EUR', 'ARS']),
  salePrice: z.number().min(0, 'Não pode ser negativo'),
})

export type ProductInput = z.infer<typeof productSchema>

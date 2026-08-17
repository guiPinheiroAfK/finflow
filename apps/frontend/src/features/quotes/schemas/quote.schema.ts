import { z } from 'zod'

export const quoteItemSchema = z.object({
  productId: z.string().min(1, 'Selecione um produto'),
  description: z.string().optional(),
  quantity: z.number().int().min(1, 'Mínimo 1'),
  unitCost: z.number().min(0, 'Não pode ser negativo'),
  unitSale: z.number().min(0, 'Não pode ser negativo'),
  travelDate: z.string().optional(),
})

export const quoteSchema = z.object({
  customerId: z.string().min(1, 'Selecione um cliente'),
  validUntil: z.string().optional(),
  notes: z.string().optional(),
  items: z.array(quoteItemSchema).min(1, 'Adicione ao menos um item'),
})

export type QuoteItemInput = z.infer<typeof quoteItemSchema>
export type QuoteInput = z.infer<typeof quoteSchema>

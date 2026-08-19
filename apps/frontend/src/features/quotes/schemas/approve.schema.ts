import { z } from 'zod'

export const approveQuoteSchema = z.object({
  paymentMethod: z.enum(['DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'BOLETO', 'TRANSFERENCIA']),
  installments: z.number().int().min(1, 'Mínimo 1'),
})

export type ApproveQuoteInput = z.infer<typeof approveQuoteSchema>

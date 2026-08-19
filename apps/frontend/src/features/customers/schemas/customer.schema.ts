import { z } from 'zod'

export const customerSchema = z.object({
  type: z.enum(['PESSOA_FISICA', 'PESSOA_JURIDICA']),
  name: z.string().min(1, 'Nome obrigatório'),
  document: z.string().min(1, 'Documento obrigatório'),
  email: z.string().email('E-mail inválido').optional().or(z.literal('')),
  phone: z.string().optional(),
  address: z
    .object({
      street: z.string().optional(),
      number: z.string().optional(),
      city: z.string().optional(),
      state: z.string().optional(),
      zip: z.string().optional(),
    })
    .optional(),
  tags: z.array(z.string()).optional(),
})

export type CustomerInput = z.infer<typeof customerSchema>

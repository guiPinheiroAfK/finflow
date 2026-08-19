import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card'

export function KpiCard({
  title,
  value,
  hint,
  tone = 'default',
}: {
  title: string
  value: string
  hint?: string
  tone?: 'default' | 'warning' | 'success'
}) {
  const toneClass = {
    default: 'text-[var(--foreground)]',
    warning: 'text-amber-600 dark:text-amber-400',
    success: 'text-green-600 dark:text-green-400',
  }[tone]

  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm font-medium text-[var(--muted-foreground)]">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className={`text-2xl font-semibold ${toneClass}`}>{value}</p>
        {hint && <p className="mt-1 text-xs text-[var(--muted-foreground)]">{hint}</p>}
      </CardContent>
    </Card>
  )
}

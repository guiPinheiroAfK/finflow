export function PlaceholderPage({ title }: { title: string }) {
  return (
    <div className="flex flex-col gap-2">
      <h1 className="text-xl font-semibold">{title}</h1>
      <p className="text-sm text-[var(--muted-foreground)]">Em construção -- ver roadmap no README.</p>
    </div>
  )
}

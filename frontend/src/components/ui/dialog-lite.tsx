import type { ReactNode } from 'react'

// 가벼운 모달 (radix 없이 최소 구현). 배경 클릭 또는 ✕로 닫힌다.
export default function Modal({
  open,
  onClose,
  title,
  children,
}: {
  open: boolean
  onClose: () => void
  title: string
  children: ReactNode
}) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4" role="dialog" aria-modal="true">
      <div className="absolute inset-0 bg-black/30" onClick={onClose} />
      <div className="relative z-10 w-full max-w-md rounded-2xl bg-card p-6 shadow-soft">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="font-display text-xl font-bold">{title}</h2>
          <button onClick={onClose} aria-label="닫기" className="text-muted-foreground hover:text-foreground">✕</button>
        </div>
        {children}
      </div>
    </div>
  )
}

import type { ReactNode } from 'react'

// 로그인/회원가입 공통 껍데기 (가운데 좁은 카드)
export default function AuthShell({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="mx-auto max-w-sm py-10">
      <h1 className="mb-6 text-center font-display text-3xl font-bold text-primary">{title}</h1>
      <div className="rounded-2xl bg-card p-6 shadow-card">{children}</div>
    </div>
  )
}

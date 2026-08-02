import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'

// 관리자 가드: 로그인 + role이 ADMIN이어야 통과.
//  (일반 사용자가 /admin에 들어오면 홈으로 돌려보냄. 백엔드도 403으로 막지만 UI에서도 차단)
export default function RequireAdmin({ children }: { children: ReactNode }) {
  const token = useAuthStore((s) => s.token)
  const member = useAuthStore((s) => s.member)
  if (!token) return <Navigate to="/login" replace />
  if (member?.role !== 'ADMIN') return <Navigate to="/" replace />
  return <>{children}</>
}

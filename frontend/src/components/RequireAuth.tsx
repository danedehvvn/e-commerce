import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'

// 인증 가드: 로그인 안 했으면 로그인 페이지로 보낸다.
//  state.from에 원래 가려던 경로를 담아, 로그인 후 되돌아오게 한다.
export default function RequireAuth({ children }: { children: ReactNode }) {
  const token = useAuthStore((s) => s.token)
  const location = useLocation()
  if (!token) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }
  return <>{children}</>
}

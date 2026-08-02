import { useState } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { api } from '@/lib/api'
import { useAuthStore } from '@/store/authStore'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import AuthShell from '@/components/AuthShell'

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const login = useAuthStore((s) => s.login)

  const [email, setEmail] = useState('user@example.com')
  const [password, setPassword] = useState('user1234')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  // 원래 가려던 경로 (없으면 홈)
  const from = (location.state as { from?: string } | null)?.from ?? '/'

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      // 1) 로그인 → 토큰
      const res = await api.post('/api/auth/login', { email, password })
      const token = res.data.accessToken as string
      // 2) 토큰을 먼저 저장(인터셉터가 헤더에 붙이도록) 후 내 정보 조회
      useAuthStore.setState({ token })
      const me = await api.get('/api/members/me')
      // 3) 토큰+회원정보를 전역 상태에 반영 (헤더가 즉시 갱신됨)
      login(token, me.data)
      navigate(from, { replace: true })
    } catch (err) {
      setError((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '로그인에 실패했어요.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell title="로그인">
      <form onSubmit={submit} className="space-y-3">
        <Input type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
        <Input type="password" placeholder="비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <p className="text-sm text-destructive">{error}</p>}
        <Button type="submit" className="w-full" disabled={loading}>{loading ? '로그인 중...' : '로그인'}</Button>
      </form>
      <p className="mt-4 text-center text-sm text-muted-foreground">
        계정이 없으신가요? <Link to="/signup" className="text-primary">회원가입</Link>
      </p>
      <p className="mt-2 text-center text-xs text-muted-foreground">데모 계정: user@example.com / user1234</p>
    </AuthShell>
  )
}

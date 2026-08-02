import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import AuthShell from '@/components/AuthShell'

export default function Signup() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', name: '' })
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const set = (key: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }))

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await api.post('/api/auth/signup', form)
      // 가입 성공 → 로그인 페이지로
      navigate('/login')
    } catch (err) {
      setError((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '회원가입에 실패했어요.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthShell title="회원가입">
      <form onSubmit={submit} className="space-y-3">
        <Input type="email" placeholder="이메일" value={form.email} onChange={set('email')} />
        <Input type="password" placeholder="비밀번호 (8자 이상)" value={form.password} onChange={set('password')} />
        <Input placeholder="이름" value={form.name} onChange={set('name')} />
        {error && <p className="text-sm text-destructive">{error}</p>}
        <Button type="submit" className="w-full" disabled={loading}>{loading ? '가입 중...' : '회원가입'}</Button>
      </form>
      <p className="mt-4 text-center text-sm text-muted-foreground">
        이미 계정이 있으신가요? <Link to="/login" className="text-primary">로그인</Link>
      </p>
    </AuthShell>
  )
}

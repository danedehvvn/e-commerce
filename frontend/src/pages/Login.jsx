import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiPost } from '../api'
import { setToken } from '../auth'

// 로그인: 성공 시 JWT를 저장하고 홈으로.
export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('user@example.com')
  const [password, setPassword] = useState('user1234')
  const [error, setError] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      const res = await apiPost('/api/auth/login', { email, password })
      setToken(res.accessToken) // 토큰 저장 → 이후 요청에 자동 첨부(api.js)
      navigate('/')
    } catch (err) {
      setError(err.message || '로그인 실패')
    }
  }

  return (
    <div>
      <h2>로그인</h2>
      <form onSubmit={submit} className="card" style={{ maxWidth: 360 }}>
        <div style={{ marginBottom: 8 }}>
          <input value={email} onChange={(e) => setEmail(e.target.value)}
                 placeholder="이메일" style={{ width: '100%' }} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                 placeholder="비밀번호" style={{ width: '100%' }} />
        </div>
        {error && <p className="error">{error}</p>}
        <button type="submit">로그인</button>
        <p className="muted">시드 계정: user@example.com / user1234</p>
      </form>
    </div>
  )
}

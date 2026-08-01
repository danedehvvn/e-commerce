import { getToken, clearToken } from './auth'

// 백엔드 주소 (환경변수에서 읽음)
const BASE = import.meta.env.VITE_API_BASE_URL

// 모든 API 호출을 감싸는 공통 함수.
//  - 저장된 JWT가 있으면 Authorization 헤더에 자동 첨부
//  - 에러 응답({status, message})을 통일된 형태로 throw
//  - 401이면 토큰을 비운다(만료/무효)
export async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }

  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(`${BASE}${path}`, { ...options, headers })

  if (res.status === 401) {
    clearToken() // 인증 만료 → 토큰 제거 (호출한 쪽에서 로그인 페이지로 유도)
  }

  const text = await res.text()
  const data = text ? JSON.parse(text) : null

  if (!res.ok) {
    // 백엔드의 { status, message } 형식을 그대로 에러로 던진다
    throw data || { status: res.status, message: '요청 실패' }
  }
  return data
}

// 편의 메서드
export const apiGet = (path) => api(path)
export const apiPost = (path, body) => api(path, { method: 'POST', body: JSON.stringify(body) })
export const apiPatch = (path, body) => api(path, { method: 'PATCH', body: JSON.stringify(body) })
export const apiDelete = (path) => api(path, { method: 'DELETE' })

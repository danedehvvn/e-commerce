import axios from 'axios'
import { useAuthStore } from '@/store/authStore'

// 백엔드와 통신하는 공통 axios 인스턴스.
//  기본 URL을 환경변수에서 읽어, 화면마다 주소를 반복해 적지 않는다.
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
})

// ── 요청 인터셉터 ──
// 모든 요청이 나가기 직전에 가로채, 저장된 JWT를 Authorization 헤더에 자동으로 붙인다.
//   → 화면마다 헤더를 수동으로 넣을 필요가 없다. (인증 로직을 한 곳에 모음)
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ── 응답 인터셉터 ──
// 모든 응답을 가로채, 401(인증 만료/무효)이면 로그아웃 처리한다.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout()
    }
    return Promise.reject(error)
  },
)

import { create } from 'zustand'
import { persist } from 'zustand/middleware'

// 로그인한 회원 정보(간단히)
export interface Member {
  id: number
  email: string
  name: string
  role: 'USER' | 'ADMIN'
}

interface AuthState {
  token: string | null
  member: Member | null
  login: (token: string, member: Member) => void
  logout: () => void
  isLoggedIn: () => boolean
}

// 전역 로그인 상태를 Zustand로 관리한다.
//  왜 전역 상태? 헤더(로그인 버튼), 장바구니, 주문 등 "여러 화면"이 로그인 여부를 공유해야 한다.
//  화면마다 따로 들고 있으면 동기화가 깨진다 → 한 곳(store)에 두고 모두가 구독한다.
//  persist 미들웨어: localStorage에 저장해 새로고침해도 로그인이 유지된다.
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      member: null,
      login: (token, member) => set({ token, member }),
      logout: () => set({ token: null, member: null }),
      isLoggedIn: () => !!get().token,
    }),
    { name: 'auth-storage' },
  ),
)

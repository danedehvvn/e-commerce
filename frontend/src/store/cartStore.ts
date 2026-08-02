import { create } from 'zustand'
import { api } from '@/lib/api'

interface CartState {
  count: number             // 헤더 뱃지에 보여줄 장바구니 담긴 개수
  refresh: () => Promise<void>
  reset: () => void
}

// 장바구니 개수 전역 상태.
//  헤더의 장바구니 뱃지가 어느 화면에서 담든 바로 갱신되도록, 개수를 전역으로 둔다.
//  실제 장바구니 데이터는 백엔드에 있고, 여기선 "개수"만 캐싱해 헤더에 표시한다.
export const useCartStore = create<CartState>((set) => ({
  count: 0,
  refresh: async () => {
    try {
      const res = await api.get('/api/cart')
      set({ count: res.data.length })
    } catch {
      set({ count: 0 }) // 비로그인 등으로 실패하면 0
    }
  },
  reset: () => set({ count: 0 }),
}))

import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import { useAuthStore } from '@/store/authStore'
import { useCartStore } from '@/store/cartStore'
import { Button } from '@/components/ui/button'
import { cn, formatPrice } from '@/lib/utils'
import type { Product } from '@/types'

// 이미지가 아직 없어 상품 id로 브랜드 톤 배경 + 이모지 플레이스홀더를 돌려 쓴다.
const PASTELS = ['bg-butter', 'bg-sky', 'bg-blush', 'bg-sage', 'bg-peri', 'bg-butter-soft']
const EMOJIS = ['🧺', '🍮', '🧁', '🎀', '🌿', '🫧', '🧷', '📎']

// 재사용 상품 카드 (홈·목록에서 공통 사용)
export default function ProductCard({ product }: { product: Product }) {
  const navigate = useNavigate()
  const token = useAuthStore((s) => s.token)
  const refreshCart = useCartStore((s) => s.refresh)
  const [added, setAdded] = useState(false)

  const soldOut = product.stockQuantity < 1 || product.status !== 'ON_SALE'

  const addToCart = async (e: React.MouseEvent) => {
    e.preventDefault() // 카드 전체가 링크라, 담기 버튼 클릭 시 상세 이동을 막는다
    if (!token) return navigate('/login')
    try {
      await api.post('/api/cart', { productId: product.id, quantity: 1 })
      await refreshCart()
      setAdded(true)
      setTimeout(() => setAdded(false), 1200)
    } catch (err) {
      if ((err as { response?: { status?: number } })?.response?.status === 401) navigate('/login')
    }
  }

  const pastel = PASTELS[product.id % PASTELS.length]
  const emoji = EMOJIS[product.id % EMOJIS.length]

  return (
    <Link
      to={`/products/${product.id}`}
      className="group block overflow-hidden rounded-2xl bg-card shadow-card transition-transform hover:-translate-y-1"
    >
      <div className={cn('flex aspect-square items-center justify-center text-5xl', pastel)}>
        <span className="transition-transform group-hover:scale-110">{emoji}</span>
      </div>
      <div className="p-3">
        <p className="text-xs text-muted-foreground">{product.categoryName}</p>
        <p className="truncate font-medium">{product.name}</p>
        <div className="mt-1 flex items-center justify-between">
          <span className="font-display text-lg">{formatPrice(product.price)}</span>
          <Button
            size="sm"
            variant={added ? 'secondary' : 'soft'}
            onClick={addToCart}
            disabled={soldOut}
          >
            {soldOut ? '품절' : added ? '담음!' : '담기'}
          </Button>
        </div>
      </div>
    </Link>
  )
}

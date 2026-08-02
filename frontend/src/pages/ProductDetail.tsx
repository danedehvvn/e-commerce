import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { formatPrice } from '@/lib/utils'
import { useAuthStore } from '@/store/authStore'
import { useCartStore } from '@/store/cartStore'
import type { Product } from '@/types'

export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const token = useAuthStore((s) => s.token)
  const refreshCart = useCartStore((s) => s.refresh)

  const [product, setProduct] = useState<Product | null>(null)
  const [qty, setQty] = useState(1)
  const [notFound, setNotFound] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [msg, setMsg] = useState<string | null>(null)

  useEffect(() => {
    api
      .get<Product>(`/api/products/${id}`)
      .then((r) => setProduct(r.data))
      .catch((e) => {
        if ((e as { response?: { status?: number } })?.response?.status === 404) setNotFound(true)
        else setError('상품을 불러오지 못했어요.')
      })
  }, [id])

  const addToCart = async () => {
    if (!token) return navigate('/login', { state: { from: `/products/${id}` } })
    try {
      await api.post('/api/cart', { productId: Number(id), quantity: qty })
      await refreshCart()
      setMsg('장바구니에 담았어요!')
    } catch (e) {
      if ((e as { response?: { status?: number } })?.response?.status === 401) navigate('/login')
      else setError('담기에 실패했어요.')
    }
  }

  if (notFound)
    return (
      <div className="py-16 text-center">
        <p className="text-lg">찾는 상품이 없어요 🥲</p>
        <Button className="mt-4" onClick={() => navigate('/products')}>목록으로</Button>
      </div>
    )
  if (error) return <p className="py-16 text-center text-destructive">{error}</p>
  if (!product) return <p className="py-16 text-center text-muted-foreground">불러오는 중...</p>

  const soldOut = product.stockQuantity < 1 || product.status !== 'ON_SALE'

  return (
    <div className="grid gap-8 md:grid-cols-2">
      <div className="flex aspect-square items-center justify-center rounded-3xl bg-butter text-8xl">🧺</div>
      <div>
        <p className="text-sm text-muted-foreground">{product.categoryName}</p>
        <h1 className="mt-1 font-display text-3xl font-bold">{product.name}</h1>
        <p className="mt-3 font-display text-3xl text-primary">{formatPrice(product.price)}</p>
        <p className="mt-4 whitespace-pre-line text-foreground/80">{product.description}</p>
        <p className="mt-2 text-sm text-muted-foreground">재고 {product.stockQuantity}개</p>

        <div className="mt-6 flex items-center gap-3">
          <div className="flex items-center rounded-full border border-border">
            <button className="px-3 py-2 text-lg" onClick={() => setQty((q) => Math.max(1, q - 1))} aria-label="수량 감소">−</button>
            <span className="w-8 text-center">{qty}</span>
            <button className="px-3 py-2 text-lg" onClick={() => setQty((q) => q + 1)} aria-label="수량 증가">+</button>
          </div>
          <Button size="lg" onClick={addToCart} disabled={soldOut}>
            {soldOut ? '품절' : '장바구니 담기'}
          </Button>
        </div>

        {msg && (
          <p className="mt-3 text-sm text-primary">
            {msg}{' '}
            <button className="underline" onClick={() => navigate('/cart')}>장바구니 보기</button>
          </p>
        )}
      </div>
    </div>
  )
}

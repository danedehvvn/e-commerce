import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { formatPrice } from '@/lib/utils'
import { useCartStore } from '@/store/cartStore'
import type { CartItem } from '@/types'

export default function Cart() {
  const navigate = useNavigate()
  const refreshCart = useCartStore((s) => s.refresh)
  const [items, setItems] = useState<CartItem[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [ordering, setOrdering] = useState(false)

  const load = () => {
    api
      .get<CartItem[]>('/api/cart')
      .then((r) => setItems(r.data))
      .catch((e) => {
        if ((e as { response?: { status?: number } })?.response?.status === 401) navigate('/login')
        else setError('장바구니를 불러오지 못했어요.')
      })
  }
  useEffect(load, [])

  const addOne = async (productId: number) => {
    await api.post('/api/cart', { productId, quantity: 1 })
    await refreshCart()
    load()
  }
  const remove = async (cartItemId: number) => {
    await api.delete(`/api/cart/${cartItemId}`)
    await refreshCart()
    load()
  }

  const order = async () => {
    if (!items) return
    setOrdering(true)
    setError(null)
    try {
      const body = { items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })) }
      const res = await api.post('/api/orders', body)
      // 주문했으니 장바구니 비우기
      for (const i of items) await api.delete(`/api/cart/${i.cartItemId}`)
      await refreshCart()
      navigate('/order-complete', { state: { order: res.data } })
    } catch (e) {
      // 재고 부족 등 백엔드 에러 메시지 표시
      setError((e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '주문에 실패했어요.')
    } finally {
      setOrdering(false)
    }
  }

  if (error && !items) return <p className="py-16 text-center text-destructive">{error}</p>
  if (!items) return <p className="py-16 text-center text-muted-foreground">불러오는 중...</p>
  if (items.length === 0)
    return (
      <div className="py-16 text-center">
        <p className="text-lg">장바구니가 비었어요 🧺</p>
        <Button className="mt-4" onClick={() => navigate('/products')}>상품 구경하기</Button>
      </div>
    )

  const total = items.reduce((sum, i) => sum + i.totalPrice, 0)

  return (
    <div className="space-y-4">
      <h1 className="font-display text-2xl font-bold">장바구니</h1>

      {items.map((i) => (
        <div key={i.cartItemId} className="flex items-center gap-4 rounded-2xl bg-card p-4 shadow-card">
          <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-xl bg-butter text-2xl">🧺</div>
          <div className="min-w-0 flex-1">
            <p className="truncate font-medium">{i.productName}</p>
            <p className="text-sm text-muted-foreground">{formatPrice(i.price)}</p>
          </div>
          <div className="flex items-center gap-2">
            <span className="text-sm">{i.quantity}개</span>
            <Button size="sm" variant="soft" onClick={() => addOne(i.productId)}>+1</Button>
          </div>
          <span className="hidden w-24 text-right font-display text-lg text-primary sm:block">{formatPrice(i.totalPrice)}</span>
          <button className="text-sm text-muted-foreground hover:text-destructive" onClick={() => remove(i.cartItemId)}>삭제</button>
        </div>
      ))}

      {error && <p className="text-sm text-destructive">{error}</p>}

      <div className="flex items-center justify-between rounded-2xl bg-butter-soft p-4">
        <span className="font-medium">합계</span>
        <span className="font-display text-2xl font-bold text-primary">{formatPrice(total)}</span>
      </div>
      <Button size="lg" className="w-full" onClick={order} disabled={ordering}>
        {ordering ? '주문 중...' : '주문하기'}
      </Button>
    </div>
  )
}

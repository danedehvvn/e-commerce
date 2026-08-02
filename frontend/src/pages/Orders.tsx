import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { formatPrice } from '@/lib/utils'
import type { Order, Page } from '@/types'

// 주문 상태 코드를 한글 라벨로
const STATUS_LABEL: Record<string, string> = {
  PAYMENT_WAITING: '결제대기',
  PAID: '결제완료',
  SHIPPING: '배송중',
  DELIVERED: '배송완료',
  CANCELED: '취소',
}

export default function Orders() {
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [data, setData] = useState<Page<Order> | null>(null)

  useEffect(() => {
    api
      .get<Page<Order>>(`/api/orders?page=${page}&size=5`)
      .then((r) => setData(r.data))
      .catch((e) => {
        if ((e as { response?: { status?: number } })?.response?.status === 401) navigate('/login')
      })
  }, [page, navigate])

  if (!data) return <p className="py-16 text-center text-muted-foreground">불러오는 중...</p>
  if (data.content.length === 0)
    return (
      <div className="py-16 text-center">
        <p className="text-lg">주문 내역이 없어요 🧾</p>
        <Button className="mt-4" onClick={() => navigate('/products')}>상품 구경하기</Button>
      </div>
    )

  return (
    <div className="space-y-4">
      <h1 className="font-display text-2xl font-bold">주문 내역</h1>

      {data.content.map((o) => (
        <div key={o.orderId} className="rounded-2xl bg-card p-4 shadow-card">
          <div className="flex items-center gap-3">
            <span className="font-medium">주문 #{o.orderId}</span>
            <Badge variant="soft">{STATUS_LABEL[o.status] ?? o.status}</Badge>
            <span className="flex-1" />
            <span className="font-display text-lg text-primary">{formatPrice(o.totalPrice)}</span>
          </div>
          <p className="mt-1 text-sm text-muted-foreground">
            {o.items.map((it) => `${it.productName} ×${it.count}`).join(', ')}
          </p>
        </div>
      ))}

      <div className="flex items-center justify-center gap-3 pt-2">
        <Button variant="outline" size="sm" disabled={data.first} onClick={() => setPage((p) => p - 1)}>이전</Button>
        <span className="text-sm text-muted-foreground">{data.number + 1} / {data.totalPages}</span>
        <Button variant="outline" size="sm" disabled={data.last} onClick={() => setPage((p) => p + 1)}>다음</Button>
      </div>
    </div>
  )
}

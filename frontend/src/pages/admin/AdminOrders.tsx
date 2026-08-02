import { useEffect, useState } from 'react'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { formatPrice, cn } from '@/lib/utils'
import AdminNav from '@/components/admin/AdminNav'
import type { Page } from '@/types'

// 어드민 주문 목록은 요약 DTO (항목 없음)
interface OrderSummary {
  orderId: number
  memberId: number
  status: string
  totalPrice: number
  orderedAt: string
}

const LABEL: Record<string, string> = {
  PAYMENT_WAITING: '결제대기',
  PAID: '결제완료',
  SHIPPING: '배송중',
  DELIVERED: '배송완료',
  CANCELED: '취소',
}

// 상태별 허용 전이 (백엔드 OrderStatus.canTransitionTo와 동일하게)
const NEXT: Record<string, string[]> = {
  PAYMENT_WAITING: ['PAID', 'CANCELED'],
  PAID: ['SHIPPING', 'CANCELED'],
  SHIPPING: ['DELIVERED'],
  DELIVERED: [],
  CANCELED: [],
}

const FILTERS = ['', 'PAYMENT_WAITING', 'PAID', 'SHIPPING', 'DELIVERED', 'CANCELED']

export default function AdminOrders() {
  const [page, setPage] = useState(0)
  const [status, setStatus] = useState('')
  const [data, setData] = useState<Page<OrderSummary> | null>(null)

  const load = () => {
    const q = new URLSearchParams({ page: String(page), size: '10' })
    if (status) q.set('status', status)
    api.get<Page<OrderSummary>>(`/api/admin/orders?${q.toString()}`).then((r) => setData(r.data))
  }
  useEffect(() => { load() }, [page, status])

  const change = async (id: number, next: string) => {
    await api.patch(`/api/admin/orders/${id}/status`, { status: next })
    load()
  }

  return (
    <div>
      <AdminNav />
      <h1 className="mb-4 font-display text-2xl font-bold">주문 관리</h1>

      {/* 상태 필터 */}
      <div className="mb-4 flex flex-wrap gap-2">
        {FILTERS.map((f) => (
          <button
            key={f || 'all'}
            onClick={() => { setPage(0); setStatus(f) }}
            className={cn(
              'rounded-full px-3 py-1 text-sm transition-colors',
              status === f ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground hover:bg-accent',
            )}
          >
            {f ? LABEL[f] : '전체'}
          </button>
        ))}
      </div>

      <div className="overflow-x-auto rounded-2xl bg-card shadow-card">
        <table className="w-full min-w-[640px] text-sm">
          <thead className="border-b border-border text-left text-muted-foreground">
            <tr>
              <th className="p-3">주문</th>
              <th className="p-3">회원</th>
              <th className="p-3">금액</th>
              <th className="p-3">상태</th>
              <th className="p-3">상태 전이</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((o) => (
              <tr key={o.orderId} className="border-b border-border/60">
                <td className="p-3 font-medium">#{o.orderId}</td>
                <td className="p-3 text-muted-foreground">회원 {o.memberId}</td>
                <td className="p-3">{formatPrice(o.totalPrice)}</td>
                <td className="p-3"><Badge variant="soft">{LABEL[o.status] ?? o.status}</Badge></td>
                <td className="p-3">
                  <div className="flex gap-1">
                    {NEXT[o.status]?.length ? (
                      NEXT[o.status].map((n) => (
                        <Button
                          key={n}
                          size="sm"
                          variant={n === 'CANCELED' ? 'outline' : 'default'}
                          onClick={() => change(o.orderId, n)}
                        >
                          {LABEL[n]}
                        </Button>
                      ))
                    ) : (
                      <span className="text-muted-foreground">완료</span>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data && (
        <div className="mt-4 flex items-center justify-center gap-3">
          <Button variant="outline" size="sm" disabled={data.first} onClick={() => setPage((p) => p - 1)}>이전</Button>
          <span className="text-sm text-muted-foreground">{data.number + 1} / {data.totalPages}</span>
          <Button variant="outline" size="sm" disabled={data.last} onClick={() => setPage((p) => p + 1)}>다음</Button>
        </div>
      )}
    </div>
  )
}

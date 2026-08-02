import { useLocation, Link, Navigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { formatPrice } from '@/lib/utils'
import type { Order } from '@/types'

// 주문 완료 화면. 장바구니에서 넘긴 주문 결과(state.order)를 보여준다.
//  (직접 URL로 들어오면 주문 정보가 없으니 주문내역으로 보냄)
export default function OrderComplete() {
  const location = useLocation()
  const order = (location.state as { order?: Order } | null)?.order
  if (!order) return <Navigate to="/orders" replace />

  return (
    <div className="mx-auto max-w-md py-10 text-center">
      <div className="text-6xl">🎉</div>
      <h1 className="mt-4 font-display text-2xl font-bold">주문 완료!</h1>
      <p className="mt-1 text-muted-foreground">주문번호 #{order.orderId}</p>

      <div className="mt-6 rounded-2xl bg-card p-5 text-left shadow-card">
        {order.items.map((it, idx) => (
          <div key={idx} className="flex justify-between py-1 text-sm">
            <span>{it.productName} ×{it.count}</span>
            <span>{formatPrice(it.totalPrice)}</span>
          </div>
        ))}
        <div className="mt-2 flex justify-between border-t border-border pt-2 font-bold">
          <span>합계</span>
          <span className="text-primary">{formatPrice(order.totalPrice)}</span>
        </div>
      </div>

      <div className="mt-6 flex gap-3">
        <Button variant="outline" className="flex-1" asChild><Link to="/products">계속 쇼핑</Link></Button>
        <Button className="flex-1" asChild><Link to="/orders">주문 내역</Link></Button>
      </div>
    </div>
  )
}

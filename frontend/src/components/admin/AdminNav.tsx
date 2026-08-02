import { NavLink } from 'react-router-dom'
import { cn } from '@/lib/utils'

const TABS = [
  { to: '/admin/products', label: '상품 관리' },
  { to: '/admin/orders', label: '주문 관리' },
]

// 어드민 상단 탭 네비
export default function AdminNav() {
  return (
    <div className="mb-6 flex gap-2">
      {TABS.map((t) => (
        <NavLink
          key={t.to}
          to={t.to}
          className={({ isActive }) =>
            cn(
              'rounded-full px-4 py-1.5 text-sm transition-colors',
              isActive ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground hover:bg-accent',
            )
          }
        >
          {t.label}
        </NavLink>
      ))}
    </div>
  )
}

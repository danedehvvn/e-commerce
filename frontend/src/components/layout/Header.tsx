import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ShoppingBag, Search, User } from 'lucide-react'
import { api } from '@/lib/api'
import { useAuthStore } from '@/store/authStore'
import { useCartStore } from '@/store/cartStore'
import { Button } from '@/components/ui/button'
import type { Category } from '@/types'

// 공통 헤더: 로고 · 검색 · 장바구니(개수 뱃지) · 로그인/로그아웃 · 카테고리 메뉴.
export default function Header() {
  const navigate = useNavigate()
  const token = useAuthStore((s) => s.token)
  const member = useAuthStore((s) => s.member)
  const logout = useAuthStore((s) => s.logout)
  const count = useCartStore((s) => s.count)
  const refreshCart = useCartStore((s) => s.refresh)

  const [categories, setCategories] = useState<Category[]>([])
  const [keyword, setKeyword] = useState('')
  const isLoggedIn = !!token

  useEffect(() => {
    api.get<Category[]>('/api/categories').then((r) => setCategories(r.data)).catch(() => {})
  }, [])

  // 로그인 상태면 장바구니 개수를 불러와 뱃지에 반영
  useEffect(() => {
    if (isLoggedIn) refreshCart()
  }, [isLoggedIn, refreshCart])

  const onSearch = (e: React.FormEvent) => {
    e.preventDefault()
    navigate(`/products?keyword=${encodeURIComponent(keyword.trim())}`)
  }

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-white/90 backdrop-blur">
      <div className="container flex h-16 items-center gap-4">
        <Link to="/" className="shrink-0" aria-label="POCHÉ 홈">
          <span className="rounded-lg bg-butter px-2.5 py-1 font-display text-2xl font-bold tracking-tight text-primary">
            POCHÉ
          </span>
        </Link>

        {/* 검색창 (모바일에선 숨김) */}
        <form onSubmit={onSearch} className="relative hidden max-w-md flex-1 md:flex">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="무엇을 찾으세요?"
            className="h-10 w-full rounded-full border border-border bg-muted/40 pl-9 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
          />
        </form>

        <div className="flex-1 md:hidden" />

        {/* 장바구니 */}
        <Link to="/cart" className="relative rounded-full p-2 hover:bg-muted" aria-label="장바구니">
          <ShoppingBag className="h-5 w-5" />
          {count > 0 && (
            <span className="absolute -right-0.5 -top-0.5 flex h-5 min-w-5 items-center justify-center rounded-full bg-primary px-1 text-[10px] text-primary-foreground">
              {count}
            </span>
          )}
        </Link>

        {/* 관리자 전용 링크 */}
        {member?.role === 'ADMIN' && (
          <Link to="/admin/products" className="hidden rounded-full bg-accent px-3 py-1.5 text-sm font-medium text-primary sm:inline-block">
            관리자
          </Link>
        )}

        {/* 로그인/로그아웃 */}
        {isLoggedIn ? (
          <div className="flex items-center gap-2">
            <span className="hidden text-sm text-muted-foreground sm:inline">{member?.name}님</span>
            <Button variant="outline" size="sm" onClick={() => { logout(); navigate('/') }}>로그아웃</Button>
          </div>
        ) : (
          <Button size="sm" asChild>
            <Link to="/login"><User className="h-4 w-4" /> 로그인</Link>
          </Button>
        )}
      </div>

      {/* 카테고리 메뉴 */}
      <nav className="container flex gap-1 overflow-x-auto pb-2">
        <Link to="/products" className="whitespace-nowrap rounded-full px-3 py-1 text-sm text-muted-foreground hover:bg-muted">
          전체
        </Link>
        {categories.map((c) => (
          <Link
            key={c.id}
            to={`/products?categoryId=${c.id}`}
            className="whitespace-nowrap rounded-full px-3 py-1 text-sm text-muted-foreground hover:bg-muted"
          >
            {c.name}
          </Link>
        ))}
      </nav>
    </header>
  )
}

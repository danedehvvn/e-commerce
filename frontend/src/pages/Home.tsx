import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import ProductCard from '@/components/product/ProductCard'
import { cn } from '@/lib/utils'
import type { Category, Page, Product } from '@/types'

// 카테고리 버블 색·이모지 (시그니처 요소)
const BUBBLES = [
  { color: 'bg-butter', emoji: '🍳' },
  { color: 'bg-sky', emoji: '✏️' },
  { color: 'bg-sage', emoji: '🛁' },
  { color: 'bg-blush', emoji: '🎀' },
  { color: 'bg-peri', emoji: '🧺' },
  { color: 'bg-butter-soft', emoji: '🌿' },
]

export default function Home() {
  const [products, setProducts] = useState<Product[]>([])
  const [categories, setCategories] = useState<Category[]>([])

  useEffect(() => {
    api.get<Page<Product>>('/api/products?size=8&sort=id,desc').then((r) => setProducts(r.data.content)).catch(() => {})
    api.get<Category[]>('/api/categories').then((r) => setCategories(r.data)).catch(() => {})
  }, [])

  return (
    <div className="space-y-10">
      {/* 히어로 (시그니처: 버터 블록 위 블루 손글씨 — 로고와 같은 조합) */}
      <section className="rounded-3xl bg-butter p-8 md:p-12">
        <p className="font-display text-4xl font-bold leading-tight text-primary md:text-5xl">
          소소하게 예쁜 잡화,<br />POCHÉ에 다 있어요
        </p>
        <p className="mt-3 text-foreground/70">문구부터 주방·욕실까지, 오늘의 작은 취향.</p>
        <Button className="mt-6" size="lg" asChild>
          <Link to="/products">구경하러 가기</Link>
        </Button>
      </section>

      {/* 카테고리 버블 */}
      <section>
        <h2 className="mb-4 font-display text-xl">카테고리</h2>
        <div className="flex flex-wrap gap-5">
          {categories.map((c, i) => (
            <Link key={c.id} to={`/products?categoryId=${c.id}`} className="group flex flex-col items-center gap-2">
              <div
                className={cn(
                  'flex h-16 w-16 items-center justify-center rounded-full text-2xl shadow-card transition-transform group-hover:animate-bounce-sm',
                  BUBBLES[i % BUBBLES.length].color,
                )}
              >
                {BUBBLES[i % BUBBLES.length].emoji}
              </div>
              <span className="text-sm">{c.name}</span>
            </Link>
          ))}
        </div>
      </section>

      {/* 추천 상품 */}
      <section>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="font-display text-xl">이주의 신상 🌟</h2>
          <Link to="/products" className="text-sm text-primary">더보기</Link>
        </div>
        <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
          {products.map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
      </section>
    </div>
  )
}

import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api } from '@/lib/api'
import ProductCard from '@/components/product/ProductCard'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import type { Category, Page, Product } from '@/types'

// 상품 목록: 카테고리 필터 + 검색(헤더에서 넘어온 keyword) + 페이징.
//  필터·검색·페이지 상태를 URL 쿼리(useSearchParams)에 두어, 새로고침/뒤로가기에도 유지된다.
export default function ProductList() {
  const [params, setParams] = useSearchParams()
  const categoryId = params.get('categoryId') ?? ''
  const keyword = params.get('keyword') ?? ''
  const page = Number(params.get('page') ?? 0)

  const [data, setData] = useState<Page<Product> | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get<Category[]>('/api/categories').then((r) => setCategories(r.data)).catch(() => {})
  }, [])

  useEffect(() => {
    const q = new URLSearchParams({ page: String(page), size: '12', sort: 'id,desc' })
    if (categoryId) q.set('categoryId', categoryId)
    if (keyword) q.set('keyword', keyword)
    setLoading(true)
    api
      .get<Page<Product>>(`/api/products?${q.toString()}`)
      .then((r) => setData(r.data))
      .catch(() => setData(null))
      .finally(() => setLoading(false))
  }, [categoryId, keyword, page])

  const goCategory = (id: string) => {
    const next = new URLSearchParams()
    if (id) next.set('categoryId', id)
    setParams(next) // page/keyword 초기화
  }
  const goPage = (p: number) => {
    const next = new URLSearchParams(params)
    next.set('page', String(p))
    setParams(next)
  }

  return (
    <div className="space-y-6">
      {/* 카테고리 필터 칩 */}
      <div className="flex flex-wrap items-center gap-2">
        <FilterChip active={!categoryId} onClick={() => goCategory('')}>전체</FilterChip>
        {categories.map((c) => (
          <FilterChip key={c.id} active={categoryId === String(c.id)} onClick={() => goCategory(String(c.id))}>
            {c.name}
          </FilterChip>
        ))}
      </div>

      {keyword && <p className="text-sm text-muted-foreground">‘{keyword}’ 검색 결과</p>}

      {loading ? (
        <p className="py-16 text-center text-muted-foreground">불러오는 중...</p>
      ) : !data || data.content.length === 0 ? (
        <p className="py-16 text-center text-muted-foreground">조건에 맞는 상품이 없어요 🥲</p>
      ) : (
        <>
          <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
            {data.content.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
          <div className="flex items-center justify-center gap-3 pt-2">
            <Button variant="outline" size="sm" disabled={data.first} onClick={() => goPage(page - 1)}>이전</Button>
            <span className="text-sm text-muted-foreground">{data.number + 1} / {data.totalPages}</span>
            <Button variant="outline" size="sm" disabled={data.last} onClick={() => goPage(page + 1)}>다음</Button>
          </div>
        </>
      )}
    </div>
  )
}

function FilterChip({ active, onClick, children }: { active: boolean; onClick: () => void; children: React.ReactNode }) {
  return (
    <button
      onClick={onClick}
      className={cn(
        'whitespace-nowrap rounded-full px-4 py-1.5 text-sm transition-colors',
        active ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground hover:bg-accent',
      )}
    >
      {children}
    </button>
  )
}

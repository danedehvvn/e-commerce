import { useEffect, useState } from 'react'
import { api } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { formatPrice, cn } from '@/lib/utils'
import AdminNav from '@/components/admin/AdminNav'
import ProductFormModal from '@/components/admin/ProductFormModal'
import type { Category, Page, Product } from '@/types'

const STATUS = [
  { value: 'ON_SALE', label: '판매중' },
  { value: 'SOLD_OUT', label: '품절' },
  { value: 'DISCONTINUED', label: '판매중지' },
]

export default function AdminProducts() {
  const [page, setPage] = useState(0)
  const [data, setData] = useState<Page<Product> | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState<Product | null>(null)
  const [stockInputs, setStockInputs] = useState<Record<number, number>>({})

  const load = () => api.get<Page<Product>>(`/api/products?page=${page}&size=10&sort=id,desc`).then((r) => setData(r.data))
  useEffect(() => { load() }, [page])
  useEffect(() => { api.get<Category[]>('/api/categories').then((r) => setCategories(r.data)) }, [])

  const changeStatus = async (id: number, status: string) => {
    await api.patch(`/api/admin/products/${id}/status`, { status })
    load()
  }
  const addStock = async (id: number) => {
    const q = stockInputs[id]
    if (!q || q < 1) return
    await api.patch(`/api/admin/products/${id}/stock`, { quantity: q })
    setStockInputs((s) => ({ ...s, [id]: 0 }))
    load()
  }

  return (
    <div>
      <AdminNav />
      <div className="mb-4 flex items-center justify-between">
        <h1 className="font-display text-2xl font-bold">상품 관리</h1>
        <Button onClick={() => { setEditing(null); setModalOpen(true) }}>+ 새 상품</Button>
      </div>

      <div className="overflow-x-auto rounded-2xl bg-card shadow-card">
        <table className="w-full min-w-[720px] text-sm">
          <thead className="border-b border-border text-left text-muted-foreground">
            <tr>
              <th className="p-3">상품</th>
              <th className="p-3">카테고리</th>
              <th className="p-3">가격</th>
              <th className="p-3">재고 (입고)</th>
              <th className="p-3">상태</th>
              <th className="p-3">관리</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((p) => (
              <tr key={p.id} className="border-b border-border/60">
                <td className="p-3 font-medium">{p.name}</td>
                <td className="p-3 text-muted-foreground">{p.categoryName}</td>
                <td className="p-3">{formatPrice(p.price)}</td>
                <td className="p-3">
                  <div className="flex items-center gap-1">
                    <span className={cn('w-8', p.stockQuantity <= 10 && 'font-medium text-destructive')}>{p.stockQuantity}</span>
                    <input
                      type="number"
                      className="h-8 w-16 rounded-lg border border-border px-2"
                      placeholder="+수량"
                      value={stockInputs[p.id] || ''}
                      onChange={(e) => setStockInputs((s) => ({ ...s, [p.id]: Number(e.target.value) }))}
                    />
                    <Button size="sm" variant="soft" onClick={() => addStock(p.id)}>입고</Button>
                  </div>
                </td>
                <td className="p-3">
                  <select
                    className="h-8 rounded-lg border border-border bg-white px-2"
                    value={p.status}
                    onChange={(e) => changeStatus(p.id, e.target.value)}
                  >
                    {STATUS.map((s) => <option key={s.value} value={s.value}>{s.label}</option>)}
                  </select>
                </td>
                <td className="p-3">
                  <Button size="sm" variant="outline" onClick={() => { setEditing(p); setModalOpen(true) }}>수정</Button>
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

      <ProductFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSaved={load}
        categories={categories}
        editing={editing}
      />
    </div>
  )
}

import { useEffect, useState } from 'react'
import Modal from '@/components/ui/dialog-lite'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { api } from '@/lib/api'
import type { Category, Product } from '@/types'

interface Props {
  open: boolean
  onClose: () => void
  onSaved: () => void
  categories: Category[]
  editing: Product | null // null이면 신규 등록, 값이 있으면 수정
}

export default function ProductFormModal({ open, onClose, onSaved, categories, editing }: Props) {
  const [name, setName] = useState('')
  const [price, setPrice] = useState(0)
  const [stock, setStock] = useState(0)
  const [desc, setDesc] = useState('')
  const [categoryId, setCategoryId] = useState<number | ''>('')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  // 모달이 열릴 때 폼 값 세팅 (수정이면 기존 값, 신규면 초기화)
  useEffect(() => {
    if (editing) {
      setName(editing.name)
      setPrice(editing.price)
      setDesc(editing.description ?? '')
      setCategoryId(editing.categoryId)
    } else {
      setName('')
      setPrice(0)
      setStock(0)
      setDesc('')
      setCategoryId(categories[0]?.id ?? '')
    }
    setError(null)
  }, [editing, open, categories])

  const save = async () => {
    setSaving(true)
    setError(null)
    try {
      if (editing) {
        // 수정: 이름·가격·설명만 (재고/상태는 별도 조작)
        await api.patch(`/api/admin/products/${editing.id}`, { name, price, description: desc })
      } else {
        await api.post('/api/admin/products', {
          categoryId,
          name,
          price,
          stockQuantity: stock,
          description: desc,
        })
      }
      onSaved()
      onClose()
    } catch (e) {
      setError((e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '저장에 실패했어요.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal open={open} onClose={onClose} title={editing ? '상품 수정' : '새 상품 등록'}>
      <div className="space-y-3">
        {!editing && (
          <select
            className="h-11 w-full rounded-xl border border-border bg-white px-3 text-sm"
            value={categoryId}
            onChange={(e) => setCategoryId(Number(e.target.value))}
          >
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        )}
        <Input placeholder="상품명" value={name} onChange={(e) => setName(e.target.value)} />
        <Input type="number" placeholder="가격" value={price} onChange={(e) => setPrice(Number(e.target.value))} />
        {!editing && (
          <Input type="number" placeholder="초기 재고" value={stock} onChange={(e) => setStock(Number(e.target.value))} />
        )}
        <textarea
          className="min-h-20 w-full rounded-xl border border-border bg-white p-3 text-sm"
          placeholder="상품 설명"
          value={desc}
          onChange={(e) => setDesc(e.target.value)}
        />
        {error && <p className="text-sm text-destructive">{error}</p>}
        <Button className="w-full" onClick={save} disabled={saving}>{saving ? '저장 중...' : '저장'}</Button>
      </div>
    </Modal>
  )
}

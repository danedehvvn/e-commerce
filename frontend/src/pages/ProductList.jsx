import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiGet } from '../api'

// 상품 목록 화면: 페이징(이전/다음) + 카테고리 필터.
export default function ProductList() {
  const [page, setPage] = useState(0)
  const [categoryId, setCategoryId] = useState('')
  const [categories, setCategories] = useState([])
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const size = 5

  // 카테고리 목록은 처음 한 번만 로드
  useEffect(() => {
    apiGet('/api/categories').then(setCategories).catch(() => {})
  }, [])

  // page나 카테고리가 바뀔 때마다 상품 목록 재조회
  useEffect(() => {
    const params = new URLSearchParams({ page, size, sort: 'price,desc' })
    if (categoryId) params.set('categoryId', categoryId)
    setError(null)
    apiGet(`/api/products?${params.toString()}`)
      .then(setData)
      .catch((e) => setError(e.message))
  }, [page, categoryId])

  if (error) return <p className="error">에러: {error}</p>
  if (!data) return <p>불러오는 중...</p>

  return (
    <div>
      <h2>상품 목록</h2>

      <div className="row" style={{ marginBottom: 12 }}>
        <select
          value={categoryId}
          onChange={(e) => { setPage(0); setCategoryId(e.target.value) }}
        >
          <option value="">전체 카테고리</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      {data.content.map((p) => (
        <div key={p.id} className="card">
          <Link to={`/products/${p.id}`}><strong>{p.name}</strong></Link> — {p.price.toLocaleString()}원
          <div className="muted">
            {p.categoryName} · 재고 {p.stockQuantity} · {p.status}
          </div>
        </div>
      ))}

      <div className="row">
        <button className="secondary" disabled={data.first} onClick={() => setPage((p) => p - 1)}>
          이전
        </button>
        <span className="muted">
          {data.number + 1} / {data.totalPages} 페이지 (총 {data.totalElements}개)
        </span>
        <button className="secondary" disabled={data.last} onClick={() => setPage((p) => p + 1)}>
          다음
        </button>
      </div>
    </div>
  )
}

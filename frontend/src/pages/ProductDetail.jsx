import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { apiGet, apiPost } from '../api'
import { isLoggedIn } from '../auth'

// 상품 상세 + 장바구니 담기.
export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [product, setProduct] = useState(null)
  const [qty, setQty] = useState(1)
  const [msg, setMsg] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    apiGet(`/api/products/${id}`).then(setProduct).catch((e) => setError(e.message))
  }, [id])

  const addToCart = async () => {
    // 로그인 안 했으면 로그인 페이지로 (담기는 인증 필요)
    if (!isLoggedIn()) return navigate('/login')
    try {
      await apiPost('/api/cart', { productId: Number(id), quantity: qty })
      setMsg('장바구니에 담았습니다.')
    } catch (e) {
      if (e.status === 401) navigate('/login')
      else setError(e.message)
    }
  }

  if (error) return <p className="error">에러: {error}</p>
  if (!product) return <p>불러오는 중...</p>

  return (
    <div>
      <button className="secondary" onClick={() => navigate(-1)}>← 뒤로</button>
      <h2>{product.name}</h2>
      <div className="card">
        <p style={{ fontSize: 20 }}>{product.price.toLocaleString()}원</p>
        <p className="muted">{product.categoryName} · 재고 {product.stockQuantity} · {product.status}</p>
        <p>{product.description}</p>
        <div className="row">
          <input
            type="number" min="1" value={qty}
            onChange={(e) => setQty(Number(e.target.value))}
            style={{ width: 80 }}
          />
          <button onClick={addToCart} disabled={product.stockQuantity < 1}>장바구니 담기</button>
        </div>
        {msg && <p className="muted">{msg}</p>}
      </div>
    </div>
  )
}

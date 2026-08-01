import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiGet } from '../api'

// 주문 내역: 내 주문 목록(페이징).
export default function Orders() {
  const navigate = useNavigate()
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    apiGet(`/api/orders?page=${page}&size=5`)
      .then(setData)
      .catch((e) => { if (e.status === 401) navigate('/login'); else setError(e.message) })
  }, [page])

  if (error) return <p className="error">에러: {error}</p>
  if (!data) return <p>불러오는 중...</p>

  return (
    <div>
      <h2>주문 내역</h2>
      {data.content.length === 0 ? (
        <p className="muted">주문이 없습니다.</p>
      ) : (
        data.content.map((o) => (
          <div key={o.orderId} className="card">
            <div className="row">
              <strong>주문 #{o.orderId}</strong>
              <span className="muted">{o.status}</span>
              <span className="spacer" />
              <span>{o.totalPrice.toLocaleString()}원</span>
            </div>
            <div className="muted">
              {o.items.map((it) => `${it.productName} ×${it.count}`).join(', ')}
            </div>
          </div>
        ))
      )}
      <div className="row">
        <button className="secondary" disabled={data.first} onClick={() => setPage((p) => p - 1)}>이전</button>
        <span className="muted">{data.number + 1} / {data.totalPages} 페이지</span>
        <button className="secondary" disabled={data.last} onClick={() => setPage((p) => p + 1)}>다음</button>
      </div>
    </div>
  )
}

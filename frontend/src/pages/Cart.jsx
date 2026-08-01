import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiGet, apiPost, apiDelete } from '../api'

// 장바구니: 조회 + 항목 삭제 + 주문하기.
export default function Cart() {
  const navigate = useNavigate()
  const [items, setItems] = useState(null)
  const [error, setError] = useState(null)

  const load = () => {
    apiGet('/api/cart')
      .then(setItems)
      .catch((e) => { if (e.status === 401) navigate('/login'); else setError(e.message) })
  }
  useEffect(load, [])

  const removeItem = async (cartItemId) => {
    await apiDelete(`/api/cart/${cartItemId}`)
    load()
  }

  const order = async () => {
    try {
      // 장바구니 항목들을 주문 요청 형태로 변환해 주문 생성
      const body = { items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })) }
      await apiPost('/api/orders', body)
      // 주문했으니 장바구니 비우기
      for (const i of items) await apiDelete(`/api/cart/${i.cartItemId}`)
      navigate('/orders') // 주문 내역으로 이동
    } catch (e) {
      if (e.status === 401) navigate('/login')
      else setError(e.message)
    }
  }

  if (error) return <p className="error">에러: {error}</p>
  if (!items) return <p>불러오는 중...</p>

  const total = items.reduce((sum, i) => sum + i.totalPrice, 0)

  return (
    <div>
      <h2>장바구니</h2>
      {items.length === 0 ? (
        <p className="muted">장바구니가 비어 있습니다.</p>
      ) : (
        <>
          <table>
            <thead>
              <tr><th>상품</th><th>단가</th><th>수량</th><th>합계</th><th></th></tr>
            </thead>
            <tbody>
              {items.map((i) => (
                <tr key={i.cartItemId}>
                  <td>{i.productName}</td>
                  <td>{i.price.toLocaleString()}원</td>
                  <td>{i.quantity}</td>
                  <td>{i.totalPrice.toLocaleString()}원</td>
                  <td><button className="secondary" onClick={() => removeItem(i.cartItemId)}>삭제</button></td>
                </tr>
              ))}
            </tbody>
          </table>
          <p><strong>총 {total.toLocaleString()}원</strong></p>
          <button onClick={order}>주문하기</button>
        </>
      )}
    </div>
  )
}

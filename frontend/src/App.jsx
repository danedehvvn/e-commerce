import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom'
import ProductList from './pages/ProductList'
import ProductDetail from './pages/ProductDetail'
import Login from './pages/Login'
import Cart from './pages/Cart'
import Orders from './pages/Orders'
import { isLoggedIn, clearToken } from './auth'

// 인증 가드: 로그인 안 했으면 로그인 페이지로 보낸다. (최소 가드)
function RequireAuth({ children }) {
  return isLoggedIn() ? children : <Navigate to="/login" replace />
}

function Nav() {
  const navigate = useNavigate()
  const loggedIn = isLoggedIn()
  return (
    <nav>
      <Link to="/"><strong>잡화 커머스</strong></Link>
      <Link to="/cart">장바구니</Link>
      <Link to="/orders">주문내역</Link>
      <span className="spacer" />
      {loggedIn ? (
        <button className="secondary" onClick={() => { clearToken(); navigate('/') }}>로그아웃</button>
      ) : (
        <Link to="/login">로그인</Link>
      )}
    </nav>
  )
}

export default function App() {
  return (
    <>
      <Nav />
      <main>
        <Routes>
          <Route path="/" element={<ProductList />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/login" element={<Login />} />
          {/* 장바구니·주문내역은 로그인 필요 */}
          <Route path="/cart" element={<RequireAuth><Cart /></RequireAuth>} />
          <Route path="/orders" element={<RequireAuth><Orders /></RequireAuth>} />
        </Routes>
      </main>
    </>
  )
}

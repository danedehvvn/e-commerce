import { Routes, Route } from 'react-router-dom'
import Layout from '@/components/layout/Layout'
import RequireAuth from '@/components/RequireAuth'
import RequireAdmin from '@/components/RequireAdmin'
import Home from '@/pages/Home'
import ProductList from '@/pages/ProductList'
import ProductDetail from '@/pages/ProductDetail'
import Login from '@/pages/Login'
import Signup from '@/pages/Signup'
import Cart from '@/pages/Cart'
import Orders from '@/pages/Orders'
import OrderComplete from '@/pages/OrderComplete'
import AdminProducts from '@/pages/admin/AdminProducts'
import AdminOrders from '@/pages/admin/AdminOrders'

// 라우팅. Layout(헤더+푸터) 안에 각 페이지가 들어간다.
//  장바구니·주문 관련은 RequireAuth로 감싸 로그인 필요.
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<ProductList />} />
        <Route path="/products/:id" element={<ProductDetail />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/cart" element={<RequireAuth><Cart /></RequireAuth>} />
        <Route path="/orders" element={<RequireAuth><Orders /></RequireAuth>} />
        <Route path="/order-complete" element={<RequireAuth><OrderComplete /></RequireAuth>} />

        {/* 관리자 (ADMIN 전용) */}
        <Route path="/admin/products" element={<RequireAdmin><AdminProducts /></RequireAdmin>} />
        <Route path="/admin/orders" element={<RequireAdmin><AdminOrders /></RequireAdmin>} />
      </Route>
    </Routes>
  )
}

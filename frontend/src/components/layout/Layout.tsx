import { Outlet } from 'react-router-dom'
import Header from './Header'
import Footer from './Footer'

// 페이지 공통 레이아웃: 헤더 + 본문(Outlet) + 푸터.
//  <Outlet/> 자리에 각 라우트의 페이지가 끼워진다. (헤더/푸터는 페이지마다 다시 안 그려도 됨)
export default function Layout() {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      <Header />
      <main className="container flex-1 py-6">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}

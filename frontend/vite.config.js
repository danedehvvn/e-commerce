import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Vite 설정. React 플러그인 사용, 개발서버 포트 5173 고정.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true, // 컨테이너/외부에서도 접근 가능하게
  },
})

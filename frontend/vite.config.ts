import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath, URL } from 'node:url'

// Vite 설정.
//  - @/ 를 src/ 로 매핑(경로 별칭) → import 시 '../../..' 대신 '@/components/...'
//  - 개발서버 5173
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    host: true,
  },
})

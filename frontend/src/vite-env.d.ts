/// <reference types="vite/client" />

// import.meta.env.VITE_API_BASE_URL 의 타입 선언
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
}
interface ImportMeta {
  readonly env: ImportMetaEnv
}

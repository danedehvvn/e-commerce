// JWT 토큰을 localStorage에 저장/조회/삭제하는 유틸.
// (브라우저를 닫아도 유지되도록 localStorage 사용)
const KEY = 'accessToken'

export const getToken = () => localStorage.getItem(KEY)
export const setToken = (token) => localStorage.setItem(KEY, token)
export const clearToken = () => localStorage.removeItem(KEY)
export const isLoggedIn = () => !!getToken()

// 백엔드 응답 타입 (API 계약)

export interface Category {
  id: number
  name: string
}

export type ProductStatus = 'ON_SALE' | 'SOLD_OUT' | 'DISCONTINUED'

export interface Product {
  id: number
  name: string
  price: number
  stockQuantity: number
  description: string
  status: ProductStatus
  categoryId: number
  categoryName: string
}

// Spring Data의 Page<T> 응답 형태
export interface Page<T> {
  content: T[]
  number: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface CartItem {
  cartItemId: number
  productId: number
  productName: string
  price: number
  quantity: number
  totalPrice: number
}

export interface OrderItem {
  productId: number
  productName: string
  orderPrice: number
  count: number
  totalPrice: number
}

export interface Order {
  orderId: number
  status: string
  totalPrice: number
  orderedAt: string
  items: OrderItem[]
}

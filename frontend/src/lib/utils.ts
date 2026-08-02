import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

// cn(): 여러 조건부 클래스를 합치고, 충돌하는 Tailwind 클래스는 뒤엣것이 이기게 병합.
//   예: cn('px-2', isActive && 'px-4') → 'px-4'
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// 가격 표시용 (1500 → "1,500원")
export function formatPrice(price: number): string {
  return `${price.toLocaleString('ko-KR')}원`
}

import * as React from 'react'
import { cn } from '@/lib/utils'

// shadcn/ui Input (우리 톤: 둥근 모서리)
export type InputProps = React.InputHTMLAttributes<HTMLInputElement>

const Input = React.forwardRef<HTMLInputElement, InputProps>(({ className, type, ...props }, ref) => (
  <input
    type={type}
    ref={ref}
    className={cn(
      'flex h-11 w-full rounded-xl border border-border bg-white px-4 text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring disabled:opacity-50',
      className,
    )}
    {...props}
  />
))
Input.displayName = 'Input'

export { Input }

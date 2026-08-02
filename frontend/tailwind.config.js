import tailwindcssAnimate from 'tailwindcss-animate'

/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    container: {
      center: true,
      padding: '1rem',
      screens: { '2xl': '1200px' },
    },
    extend: {
      colors: {
        // shadcn 시맨틱 색상 (CSS 변수 → index.css에서 정의)
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        primary: { DEFAULT: 'hsl(var(--primary))', foreground: 'hsl(var(--primary-foreground))' },
        secondary: { DEFAULT: 'hsl(var(--secondary))', foreground: 'hsl(var(--secondary-foreground))' },
        muted: { DEFAULT: 'hsl(var(--muted))', foreground: 'hsl(var(--muted-foreground))' },
        accent: { DEFAULT: 'hsl(var(--accent))', foreground: 'hsl(var(--accent-foreground))' },
        destructive: { DEFAULT: 'hsl(var(--destructive))', foreground: 'hsl(var(--destructive-foreground))' },
        card: { DEFAULT: 'hsl(var(--card))', foreground: 'hsl(var(--card-foreground))' },
        popover: { DEFAULT: 'hsl(var(--popover))', foreground: 'hsl(var(--popover-foreground))' },

        // 브랜드 색 세트 (POCHÉ: 블루 + 버터 중심, 썸네일/블록용 톤)
        cobalt: '#2B47C7',
        butter: '#F4E4A1',
        'butter-soft': '#FBF3D4',
        cream: '#FFFCF3',
        sky: '#CFE0F5',
        blush: '#F6D3C9',
        sage: '#D9E4C4',
        peri: '#D9DEF7',
      },
      fontFamily: {
        // 본문·UI는 Pretendard, 제목·로고는 손글씨 느낌의 Gaegu(로고와 결 맞춤)
        sans: ['Pretendard', 'system-ui', 'sans-serif'],
        display: ['Gaegu', 'Pretendard', 'system-ui', 'sans-serif'],
      },
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 4px)',
        sm: 'calc(var(--radius) - 8px)',
      },
      boxShadow: {
        // 블루 틴트의 부드러운 그림자
        soft: '0 6px 22px -8px rgba(43,71,199,0.28)',
        card: '0 4px 16px -8px rgba(43,71,199,0.22)',
      },
      keyframes: {
        'bounce-sm': {
          '0%,100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-4px)' },
        },
      },
      animation: {
        'bounce-sm': 'bounce-sm 0.6s ease-in-out',
      },
    },
  },
  plugins: [tailwindcssAnimate],
}

# ── 1단계: 빌드 (Node로 React 앱을 정적 파일로 번들) ──
FROM node:20-alpine AS build
WORKDIR /app

# 의존성 먼저 설치 (package.json이 안 바뀌면 이 레이어 캐시)
COPY package.json package-lock.json ./
RUN npm ci

# 소스 복사 후 프로덕션 빌드 → /app/dist 생성
#   (VITE_API_BASE_URL은 .env에서 읽혀 번들에 박힌다: 브라우저가 부를 백엔드 주소)
COPY . .
RUN npm run build

# ── 2단계: 서빙 (가벼운 nginx로 정적 파일만 제공) ──
FROM nginx:alpine
# 빌드 결과물(dist)만 nginx 웹루트로 복사 (Node·소스는 최종 이미지에 없음)
COPY --from=build /app/dist /usr/share/nginx/html
# SPA 라우팅용 nginx 설정
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

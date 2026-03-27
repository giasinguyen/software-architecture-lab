# syntax=docker/dockerfile:1
FROM node:20-alpine AS builder
WORKDIR /app

# Cache npm dependencies: layer chỉ invalidate khi package*.json thay đổi
COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline

COPY . .
RUN npm run build

# Pinned nginx version → reproducible builds
FROM nginx:1.27-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

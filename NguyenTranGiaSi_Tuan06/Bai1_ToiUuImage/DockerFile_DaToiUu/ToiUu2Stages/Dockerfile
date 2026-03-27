# ─────────────────────────────────────────────
# STAGE 1 — builder
# Làm tất cả: install, build, test
# Sau bước này → chỉ lấy artifact, vứt hết
# ─────────────────────────────────────────────
FROM node:20-alpine AS builder
 
WORKDIR /app
 
# Copy package files trước → tận dụng layer cache
# (nếu code thay đổi nhưng package.json không đổi → cache hit)
COPY package.json package-lock.json ./
 
# Cài TẤT CẢ deps (gồm devDeps để build + test)
# Không sao vì stage này sẽ bị loại bỏ hoàn toàn
RUN npm ci
 
# Copy toàn bộ source
COPY . .
 
# Build + test trong cùng 1 stage
# Nếu test fail → build dừng, image runner không được tạo
RUN npm run build && npm test
 
 
# ─────────────────────────────────────────────
# STAGE 2 — runner (image production thực sự)
# Chỉ chứa những gì cần để CHẠY app
# ─────────────────────────────────────────────
FROM node:20-alpine AS runner
 
# Non-root user → bảo mật
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
 
WORKDIR /app
 
# Chỉ copy package files để cài lại prod deps
COPY package.json package-lock.json ./
 
# Cài LẠI chỉ production deps (không có devDeps)
# Tách riêng thay vì copy node_modules từ builder
# → sạch hơn, không lọt devDeps vào image final
RUN npm ci --omit=dev
 
# Copy chỉ /dist từ stage builder
# Compiler, source .ts, jest... ở lại stage builder, không qua đây
COPY --from=builder /app/dist ./dist
 
USER appuser
 
EXPOSE 3000
 
CMD ["node", "dist/server.js"]
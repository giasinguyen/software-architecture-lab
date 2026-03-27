# ============================================================
#  DOCKERFILE ĐÃ TỐI ƯU — Multi-stage build
#  Chiến lược: mỗi stage có mục đích riêng, image final
#  chỉ chứa những gì CẦN THIẾT để chạy app
#  Kết quả: image ~180 MB (giảm ~85%)
# ============================================================


# ─────────────────────────────────────────────
# STAGE 1 — deps: cài production dependencies
# ─────────────────────────────────────────────
FROM node:20-alpine AS deps

WORKDIR /app

# Chỉ copy package files trước → tận dụng Docker layer cache
# Nếu code thay đổi nhưng package.json không đổi → bỏ qua bước này
COPY package.json package-lock.json ./

# npm ci: clean install, reproducible, nhanh hơn npm install
# --omit=dev: chỉ cài production dependencies
RUN npm ci --omit=dev


# ─────────────────────────────────────────────
# STAGE 2 — builder: cài dev deps + build source
# ─────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Copy production deps từ stage 1
COPY --from=deps /app/node_modules ./node_modules

# Copy package files
COPY package.json package-lock.json ./

# Cài thêm devDependencies (typescript, webpack...) CHỈ để build
RUN npm ci

# Copy source code
COPY . .

# Build → tạo ra /app/dist
RUN npm run build


# ─────────────────────────────────────────────
# STAGE 3 — tester: chạy test (không vào image final)
# ─────────────────────────────────────────────
FROM builder AS tester

# Chạy test trên image có đầy đủ devDeps
# Stage này dùng trong CI/CD, không deploy
RUN npm test


# ─────────────────────────────────────────────
# STAGE 4 — runner: image production THỰC SỰ
# ─────────────────────────────────────────────
FROM node:20-alpine AS runner

# Tạo user riêng, không chạy bằng root → bảo mật
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Chỉ copy những gì CẦN để chạy:
# 1. Production node_modules (từ stage deps - không có devDeps)
COPY --from=deps /app/node_modules ./node_modules

# 2. Build artifacts (từ stage builder - chỉ /dist)
COPY --from=builder /app/dist ./dist

# 3. package.json (để Node.js đọc metadata nếu cần)
COPY --from=builder /app/package.json ./package.json

# Không có: typescript compiler, webpack, jest, source .ts, .git...

# Chuyển sang non-root user
USER appuser

EXPOSE 3000

# Dùng node trực tiếp, không qua npm (ít process hơn, signal handling tốt hơn)
CMD ["node", "dist/server.js"]


# ============================================================
#  TẠI SAO TỐT HƠN?
#
#  1. node:20-alpine = ~170 MB (thay vì ~1.1 GB Debian đầy đủ)
#  2. devDependencies (tsc, webpack, jest) KHÔNG vào image final
#  3. COPY package*.json trước → layer cache hiệu quả
#  4. npm ci thay npm install → reproducible + nhanh hơn
#  5. Non-root user → bảo mật, best practice
#  6. Mỗi stage có vai trò riêng biệt, rõ ràng
#  7. Stage tester tách riêng → CI/CD có thể dừng tại đây nếu test fail
#
#  ► Build production image (bỏ qua stage tester):
#    docker build --target runner -t app-optimized .
#
#  ► Build + chạy test trong CI:
#    docker build --target tester -t app-test .
#
#  ► docker image ls → thấy ~180 MB (giảm 85% so với naive)
#
# ============================================================

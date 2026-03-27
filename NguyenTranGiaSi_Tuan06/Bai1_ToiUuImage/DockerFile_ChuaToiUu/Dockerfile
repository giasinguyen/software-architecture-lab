# ============================================================
#  DOCKERFILE CHƯA TỐI ƯU — 4 stages (naive approach)
#  Vấn đề: image cuối chứa TẤT CẢ mọi thứ từ 4 stages
#  → compiler, dev tools, test framework, toàn bộ node_modules
#  Kết quả: image ~1.2 GB, deploy chậm, attack surface lớn
# ============================================================


# ─────────────────────────────────────────────
# STAGE 1 — Cài dependencies (dev + prod lẫn lộn)
# ─────────────────────────────────────────────
FROM node:20

WORKDIR /app

# Copy toàn bộ source vào (kể cả .git, tests, docs...)
COPY . .

# Cài TẤT CẢ packages: dev deps, test deps, prod deps
# → node_modules sẽ rất nặng (~500 MB)
RUN npm install

# ─────────────────────────────────────────────
# STAGE 2 — Build source (VẪন trên cùng image béo)
# ─────────────────────────────────────────────

# Không dùng FROM mới → mọi thứ tích lũy trên 1 layer
RUN npm run build

# ─────────────────────────────────────────────
# STAGE 3 — Chạy tests (VẪN trên cùng image béo)
# ─────────────────────────────────────────────

# Jest/Mocha/Vitest được cài sẵn → chạy test tại đây
# Nhưng các tool này SẼ ĐI VÀO image production!
RUN npm test

# ─────────────────────────────────────────────
# STAGE 4 — "Chạy app" (vẫn là image node:20 đầy đủ)
# ─────────────────────────────────────────────

# Expose port
EXPOSE 3000

# Chạy app
# Image cuối chứa: compiler, npm, node_modules đầy đủ (dev+prod),
# test framework, source gốc, build artifacts... tất cả!
CMD ["node", "dist/server.js"]


# ============================================================
#  TẠI SAO XẤU?
#
#  1. node:20 base = ~1.1 GB (đầy đủ Debian + build tools)
#  2. npm install cài cả devDependencies vào image production
#  3. Không có .dockerignore → COPY . . copy cả .git, tests...
#  4. Compiler (tsc, webpack) nằm trong image final = security risk
#  5. Test framework (jest) nằm trong image final = lãng phí
#  6. Mỗi RUN tạo 1 layer mới tích lũy → image ngày càng phình
#  7. Không dùng npm ci → không reproducible build
#
#  ► docker build -t app-unoptimized .
#  ► docker image ls → thấy ~1.2 GB
# ============================================================
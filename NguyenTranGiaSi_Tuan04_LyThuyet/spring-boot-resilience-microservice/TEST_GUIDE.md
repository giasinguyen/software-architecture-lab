# 🧪 Hướng dẫn Test Resilience4j Microservice

## 🚀 Bước 1: Build và Chạy Docker

```bash
# Di chuyển vào thư mục project
cd spring-boot-resilience-microservice

# Build và start tất cả services
docker-compose up --build

# Hoặc chạy background
docker-compose up --build -d
```

**Chờ services khởi động (khoảng 30s-1 phút)**

Kiểm tra services đã chạy:
```bash
docker ps
```

Kết quả mong đợi:
```
CONTAINER ID   IMAGE                    PORTS                    NAMES
xxxx           product-service          0.0.0.0:8081->8081/tcp   product-service
xxxx           order-service            0.0.0.0:8080->8080/tcp   order-service
```

---

## 📋 Bước 2: Test Health Check

```bash
# Check Product Service
curl http://localhost:8081/api/products/status

# Check Order Service  
curl http://localhost:8080/actuator/health
```

---

## 🧪 Bước 3: Test Từng Pattern

### 1️⃣ Test RETRY (Tự động thử lại)

**Endpoint:** `GET /api/orders/retry`

```bash
curl http://localhost:8080/api/orders/retry
```

**Kết quả:**
- ✅ **SUCCESS**: Sau vài lần retry thành công
- ❌ **ERROR 503**: "Product Service unavailable after retry" (nếu retry hết lần)

**Xem logs để thấy retry:**
```bash
docker logs -f order-service
```

Bạn sẽ thấy:
```
[RETRY] Calling Product Service /unstable...
[Product Service] Request #1 - RANDOM FAIL
[RETRY] Calling Product Service /unstable... (lần 2)
[Product Service] Request #2 - OK after potential retry
```

---

### 2️⃣ Test CIRCUIT BREAKER (Ngắt mạch)

**Endpoint:** `GET /api/orders/circuit-breaker`

**Bước test:**

```bash
# Gọi 5-6 lần liên tục để circuit OPEN
for i in {1..6}; do 
  echo "Request $i:"
  curl http://localhost:8080/api/orders/circuit-breaker
  echo -e "\n---"
  sleep 1
done
```

**Kết quả mong đợi:**
- Request 1-3: ❌ ERROR (Product Service lỗi)
- Request 4-5: ❌ ERROR (Tiếp tục lỗi, tỷ lệ lỗi >50%)
- Request 6+: ❌ ERROR "Circuit breaker is OPEN" (Circuit đã ngắt, không gọi Product Service nữa!)

**Xem trạng thái Circuit:**
```bash
curl http://localhost:8080/api/orders/circuit-status
```

Response:
```json
{
  "circuitState": "OPEN",  // ← Circuit đã ngắt!
  "failureRate": "100.0%",
  "failedCalls": 5
}
```

**Reset Circuit về CLOSED:**
```bash
curl -X POST http://localhost:8080/api/orders/circuit-reset
```

---

### 3️⃣ Test RATE LIMITER (Giới hạn request)

**Endpoint:** `GET /api/orders/rate-limiter`

**Config:** 5 requests / 10 giây

```bash
# Gọi 10 lần nhanh
for i in {1..10}; do 
  echo "Request $i:"
  curl http://localhost:8080/api/orders/rate-limiter
  echo -e "\n"
done
```

**Kết quả:**
- Request 1-5: ✅ **SUCCESS**
- Request 6-10: ❌ **ERROR 503** "Rate limit exceeded!"

**Chờ 10 giây rồi test lại → OK**

---

### 4️⃣ Test BULKHEAD (Giới hạn đồng thời)

**Endpoint:** `GET /api/orders/bulkhead`

**Config:** Tối đa 3 requests cùng lúc

**Cách test (cần nhiều terminal):**

**Terminal 1:**
```bash
curl http://localhost:8080/api/orders/bulkhead
```

**Terminal 2 (ngay sau đó):**
```bash
curl http://localhost:8080/api/orders/bulkhead
```

**Terminal 3 (ngay sau đó):**
```bash
curl http://localhost:8080/api/orders/bulkhead
```

**Terminal 4 (ngay sau đó):**
```bash
curl http://localhost:8080/api/orders/bulkhead
```

**Kết quả:**
- Terminal 1-3: ✅ Thành công (nhưng delay 3 giây vì Product Service slow)
- Terminal 4: ❌ **ERROR 503** "Service overloaded - max concurrent calls reached!"

---

### 5️⃣ Test COMBINED (Kết hợp nhiều pattern)

**Endpoint:** `GET /api/orders/combined`

Áp dụng đồng thời:
- Rate Limiter (5 req/10s)
- Circuit Breaker (50% failure → OPEN)
- Retry (3 lần)

```bash
# Reset circuit trước
curl -X POST http://localhost:8080/api/orders/circuit-reset

# Test combined
for i in {1..8}; do 
  echo "Request $i:"
  curl http://localhost:8080/api/orders/combined
  echo -e "\n---"
done
```

**Kết quả:**
- Request 1-5: Có thể SUCCESS (sau retry) hoặc ERROR
- Request 6+: ❌ Rate limit hoặc Circuit OPEN

---

## 📊 Xem Logs Realtime

**Xem logs Order Service:**
```bash
docker logs -f order-service
```

**Xem logs Product Service:**
```bash
docker logs -f product-service
```

**Xem logs cả 2:**
```bash
docker-compose logs -f
```

---

## 🔍 Monitoring với Actuator

**Circuit Breakers:**
```bash
curl http://localhost:8080/actuator/circuitbreakers
```

**Rate Limiters:**
```bash
curl http://localhost:8080/actuator/ratelimiters
```

**Retries:**
```bash
curl http://localhost:8080/actuator/retries
```

**Bulkheads:**
```bash
curl http://localhost:8080/actuator/bulkheads
```

---

## 🛑 Dừng Services

```bash
# Dừng và xóa containers
docker-compose down

# Dừng + xóa volumes
docker-compose down -v

# Rebuild từ đầu
docker-compose down -v
docker-compose up --build
```

---

## 📝 Test Checklist

- [ ] Product Service health OK (port 8081)
- [ ] Order Service health OK (port 8080)
- [ ] Retry: Thấy log retry nhiều lần
- [ ] Circuit Breaker: Circuit chuyển từ CLOSED → OPEN
- [ ] Rate Limiter: Request thứ 6+ bị reject
- [ ] Bulkhead: Request thứ 4 (đồng thời) bị reject
- [ ] Combined: Nhiều pattern hoạt động cùng lúc
- [ ] Fallback: Throw exception thay vì fake data
- [ ] Logs: Rõ ràng, dễ debug

---

## 🎯 Expected Behaviors

### ✅ Khi Product Service HOẠT ĐỘNG:
- Retry: SUCCESS sau vài lần thử
- Rate Limiter: 5 request/10s OK
- Bulkhead: 3 concurrent OK

### ❌ Khi Product Service LỖI:
- Retry: ERROR sau 3 lần thử
- Circuit Breaker: OPEN sau 50% lỗi
- Response: HTTP 503 với message lỗi thực tế (KHÔNG có fake data!)

---

## 🔧 Troubleshooting

**Lỗi: Connection refused**
```bash
# Kiểm tra services
docker ps

# Xem logs lỗi
docker logs product-service
docker logs order-service
```

**Build lại khi code thay đổi:**
```bash
docker-compose down
docker-compose up --build --force-recreate
```

**Xóa cache Docker:**
```bash
docker system prune -af
docker volume prune -f
```

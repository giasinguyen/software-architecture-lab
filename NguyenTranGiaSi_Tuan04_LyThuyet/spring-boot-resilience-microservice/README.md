# 🛡️ Resilience4j Demo - Microservice với Docker

Demo Resilience4j patterns (Retry, Circuit Breaker, Rate Limiter, Bulkhead) với 2 microservices chạy trên Docker.

## 🏗️ Kiến trúc

```
┌─────────────────┐         ┌─────────────────┐
│  Client:8080    │  HTTP   │  Provider:8081  │
│  (Resilience4j) │ ───────>│  (Mock Service) │
└─────────────────┘         └─────────────────┘
        │                            │
        └────────── Docker Network ──┘
```

## 📁 Cấu trúc thư mục

```
spring-boot-resilience-microservice/
├── docker-compose.yml              # Orchestrate 2 services
├── resilience-client/
│   ├── Dockerfile                  # Client image
│   ├── pom.xml
│   └── src/
└── resilience-provider/
    ├── Dockerfile                  # Provider image
    ├── pom.xml
    └── src/
```

## 🚀 Cách chạy

### 1. Build và chạy Docker Compose

```bash
cd spring-boot-resilience-microservice
docker-compose up --build
```

### 2. Kiểm tra services đã chạy

```bash
# Xem containers
docker ps

# Xem logs
docker-compose logs -f

# Xem logs của 1 service
docker-compose logs -f resilience-client
docker-compose logs -f resilience-provider
```

### 3. Test các endpoints

**Provider (8081):**
- http://localhost:8081/api/provider/data
- http://localhost:8081/api/provider/unstable
- http://localhost:8081/api/provider/slow?delaySeconds=3
- http://localhost:8081/api/provider/always-fail

**Client (8080):**
- http://localhost:8080/api/client/retry
- http://localhost:8080/api/client/circuit
- http://localhost:8080/api/client/rate-limit
- http://localhost:8080/api/client/bulkhead
- http://localhost:8080/api/client/combined
- http://localhost:8080/api/client/status

### 4. Dừng services

```bash
# Dừng và xóa containers
docker-compose down

# Dừng, xóa containers và volumes
docker-compose down -v
```

## 🔧 Cấu hình quan trọng

### Docker Compose

```yaml
services:
  resilience-provider:
    ports: "8081:8081"
    
  resilience-client:
    ports: "8080:8080"
    environment:
      - PROVIDER_URL=http://resilience-provider:8081  # Gọi qua Docker network
    depends_on:
      - resilience-provider
```

### Client gọi Provider

```java
@Value("${provider.url}")  // Đọc từ environment variable
private String providerBaseUrl;

// Gọi: http://resilience-provider:8081/api/provider/...
restTemplate.getForObject(providerBaseUrl + "/api/provider/data", Map.class);
```

## 📊 Monitoring

**Actuator endpoints:**
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/circuitbreakers
- http://localhost:8080/actuator/ratelimiters
- http://localhost:8080/actuator/retries
- http://localhost:8080/actuator/bulkheads

## 🧪 Demo từng Pattern

### 1. Retry
```bash
curl http://localhost:8080/api/client/retry
# Xem log: Tự động retry khi lỗi
```

### 2. Circuit Breaker
```bash
# Gọi 5-6 lần liên tục
for i in {1..6}; do curl http://localhost:8080/api/client/circuit; done
# Quan sát: Lần đầu CLOSED → Lỗi nhiều → OPEN
```

### 3. Rate Limiter
```bash
# Gọi >5 lần trong 10s
for i in {1..10}; do curl http://localhost:8080/api/client/rate-limit; done
# Kết quả: 5 request OK, còn lại FALLBACK
```

### 4. Bulkhead
```bash
# Mở 5 terminal và gọi đồng thời
curl http://localhost:8080/api/client/bulkhead
# Kết quả: 3 request đầu OK, còn lại FALLBACK
```

## 🐛 Troubleshooting

**Lỗi: Connection refused**
```bash
# Kiểm tra containers
docker ps

# Xem logs lỗi
docker-compose logs
```

**Rebuild image khi code thay đổi:**
```bash
docker-compose up --build --force-recreate
```

**Xóa tất cả để build lại từ đầu:**
```bash
docker-compose down -v
docker system prune -af
docker-compose up --build
```

## 📚 So sánh với Monolith

| | Monolith | Microservice |
|---|---|---|
| Chạy | 2 terminal riêng | 1 lệnh `docker-compose up` |
| Network | localhost | Docker network |
| Provider URL | `localhost:8081` | `resilience-provider:8081` |
| Deployment | Thủ công từng service | Container orchestration |

## 🎯 Lợi ích Docker

✅ **Dễ chạy:** 1 lệnh chạy tất cả  
✅ **Cô lập:** Mỗi service trong container riêng  
✅ **Chuẩn hóa:** Môi trường giống nhau (dev, test, prod)  
✅ **Scale:** Dễ dàng tăng số instance  

## 📝 Lưu ý

- Provider phải chạy trước Client (cấu hình `depends_on` trong docker-compose)
- Network bridge tự động kết nối 2 services
- Environment variable `PROVIDER_URL` override config mặc định
- Health check để đảm bảo services ready trước khi accept traffic

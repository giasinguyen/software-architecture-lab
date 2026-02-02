## 🐳 Demo Resilience4j với Docker

### Kiến trúc
```
User → Client Container (8080) → Provider Container (8081)
           ↓ Resilience4j
       (Docker Network)
```

### Chạy nhanh

```bash
# Build và start
docker-compose up --build

# Dừng
docker-compose down
```

### Test

| Service | URL | Test gì |
|---------|-----|---------|
| **Provider** | http://localhost:8081/api/provider/data | Luôn thành công |
| **Client** | http://localhost:8080/api/client/retry | Test Retry |
| **Client** | http://localhost:8080/api/client/circuit | Test Circuit Breaker |
| **Client** | http://localhost:8080/api/client/rate-limit | Test Rate Limiter |
| **Client** | http://localhost:8080/api/client/bulkhead | Test Bulkhead |

### Khác biệt với Monolith

| | Monolith | Docker Microservice |
|---|---|---|
| Chạy | 2 terminal | `docker-compose up` |
| URL Provider | `localhost:8081` | `resilience-provider:8081` |
| Config | Hardcode | Environment variable |

### Xem logs

```bash
docker-compose logs -f resilience-client
docker-compose logs -f resilience-provider
```

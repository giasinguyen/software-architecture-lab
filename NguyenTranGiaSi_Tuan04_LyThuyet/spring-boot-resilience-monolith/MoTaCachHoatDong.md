
### Luồng hoạt động
```
User → Client (8080) → Provider (8081)
         ↓ Resilience4j
    (Retry, Circuit Breaker, Rate Limiter, Bulkhead)
```

---

### 1. Provider Service - Port 8081
**Vai trò:** Giả lập service với các tình huống lỗi

| Endpoint | Mô tả |
|----------|-------|
| `/api/provider/data` | Luôn thành công |
| `/api/provider/unstable` | 50% lỗi ngẫu nhiên |
| `/api/provider/slow?delaySeconds=3` | Delay n giây |
| `/api/provider/always-fail` | Luôn trả lỗi 500 |

### 2. Client Service - Port 8080
**Vai trò:** Gọi Provider với các pattern bảo vệ

| Endpoint | Pattern | Test gì |
|----------|---------|---------|
| `/api/client/retry` | Retry | Tự động thử lại |
| `/api/client/circuit` | Circuit Breaker | Ngắt mạch khi lỗi nhiều |
| `/api/client/rate-limit` | Rate Limiter | Giới hạn request/giây |
| `/api/client/bulkhead` | Bulkhead | Giới hạn request đồng thời |
| `/api/client/combined` | Kết hợp | Nhiều pattern cùng lúc |
| `/api/client/status` | - | Xem trạng thái Circuit Breaker |
| `/api/client/reset` | - | Reset Circuit Breaker về CLOSED |

---

## Các Pattern Resilience4j

### 1. RETRY (Tự động thử lại)
**Mục đích:** Khi gọi service bị lỗi tạm thời, tự động thử lại thay vì báo lỗi ngay.

**Cấu hình:**
```yaml
maxAttempts: 3          # Thử tối đa 3 lần
waitDuration: 2s        # Đợi 2 giây giữa các lần
```

**Cách hoạt động:**
```
Lần 1: Gọi Provider → Lỗi → Đợi 2s
Lần 2: Gọi lại       → Lỗi → Đợi 2s  
Lần 3: Gọi lại       → OK  → Trả kết quả
                    → Lỗi → Trả Fallback
```

---

### 2. CIRCUIT BREAKER (Ngắt mạch)
**Mục đích:** Như cầu dao điện - khi lỗi quá nhiều thì ngắt luôn, không gọi nữa.

**3 Trạng thái:**
- **CLOSED** 🟢: Bình thường, cho request qua
- **OPEN** 🔴: Ngắt mạch, từ chối tất cả request
- **HALF_OPEN** 🟡: Thử lại để kiểm tra đã OK chưa

**Cấu hình:**
```yaml
slidingWindowSize: 5              # Xét 5 request gần nhất
failureRateThreshold: 50          # Nếu ≥50% lỗi → OPEN
waitDurationInOpenState: 10s      # Đợi 10s rồi chuyển HALF_OPEN
```

**Cách hoạt động:**
```
CLOSED → (Lỗi ≥50%) → OPEN → (Đợi 10s) → HALF_OPEN
   ↑                                         ↓
   └──────────── (Test OK) ─────────────────┘
         └─ (Test lỗi) → OPEN
```

---

### 3. RATE LIMITER (Giới hạn tốc độ)
**Mục đích:** Giới hạn số lượng request trong khoảng thời gian.

**Cấu hình:**
```yaml
limitForPeriod: 5           # Cho phép 5 request
limitRefreshPeriod: 10s     # Mỗi 10 giây
```

**Cách hoạt động:**
```
10 giây đầu:  Request 1-5   → OK ✅
              Request 6-10  → Từ chối ❌
10 giây sau:  Request 11-15 → OK ✅ (reset lại)
```

---

### 4. BULKHEAD (Cách ly tài nguyên)
**Mục đích:** Giới hạn số request **đồng thời** (cùng lúc) để tránh quá tải.

**So sánh:**
- **Rate Limiter:** 100 request/phút (theo thời gian)
- **Bulkhead:** 10 request cùng lúc (theo số lượng đồng thời)

**Cấu hình:**
```yaml
maxConcurrentCalls: 3      # Tối đa 3 request cùng lúc
maxWaitDuration: 0s        # Không đợi, từ chối ngay
```

**Cách hoạt động:**
```
Tại cùng 1 thời điểm:
Request 1-3: Đang xử lý ⏳
Request 4-5: Bị từ chối ❌ (vượt giới hạn)
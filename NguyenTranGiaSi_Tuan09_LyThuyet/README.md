# Message Queue – 3 Demo Spring Boot (Java 23 + RabbitMQ)

Trích từ bài giảng **Message Queue 6 giờ**, xây dựng thành 3 demo độc lập.

---

## Cấu trúc

```
NguyenTranGiaSi_Tuan09_LyThuyet/
├── docker-compose.yml          ← Shared RabbitMQ
├── demo1-message-broker/       ← Direct Exchange + DLQ
├── demo2-point-to-point/       ← Work Queue + prefetch + ack
└── demo3-pubsub/               ← Fanout + Topic Exchange
```

---

## Bước 0 – Khởi động RabbitMQ (chạy 1 lần cho cả 3 demo)

```bash
cd NguyenTranGiaSi_Tuan09_LyThuyet
docker compose up -d
```

Truy cập Management UI: **http://localhost:15672** (admin / admin123)

---

## Demo 1 – Message Broker

> Direct Exchange routing theo log level: info / warning / error → 3 queue riêng  
> Dead Letter Queue (DLQ) nhận message bị reject

```bash
cd demo1-message-broker
./mvnw spring-boot:run
```

| Method | URL | Mô tả |
|--------|-----|-------|
| POST | `localhost:8081/api/broker/send?level=info&message=User+login` | Gửi info log |
| POST | `localhost:8081/api/broker/send?level=warning&message=Low+stock` | Gửi warning |
| POST | `localhost:8081/api/broker/send?level=error&message=Payment+fail` | Gửi error log |
| POST | `localhost:8081/api/broker/send-bad` | Gửi message sẽ bị reject → vào DLQ |

---

## Demo 2 – Point-to-Point (P2P)

> 1 Producer → 1 Queue → 2 Worker cạnh tranh nhau (round-robin + fair dispatch)  
> Manual ACK + prefetch=1 đảm bảo phân phối đều

```bash
cd demo2-point-to-point
./mvnw spring-boot:run
```

| Method | URL | Mô tả |
|--------|-----|-------|
| POST | `localhost:8082/api/p2p/hello?message=Hello+World` | Gửi hello message |
| POST | `localhost:8082/api/p2p/tasks` | Gửi 5 task vào work queue (2 worker xử lý song song) |
| POST | `localhost:8082/api/p2p/tasks/single?taskName=Export+PDF&durationMs=2000` | Gửi 1 task tùy chỉnh |

Quan sát log: Worker-1 và Worker-2 luân phiên nhận task.

---

## Demo 3 – Publish/Subscribe (Pub/Sub)

> **Fanout**: 1 publisher → 3 subscriber đều nhận (Email, Inventory, Analytics)  
> **Topic**: filter theo pattern `orders.#`, `*.created.*`, `payments.#`

```bash
cd demo3-pubsub
./mvnw spring-boot:run
```

| Method | URL | Mô tả |
|--------|-----|-------|
| POST | `localhost:8083/api/pubsub/fanout` | Broadcast order event đến TẤT CẢ subscriber |
| POST | `localhost:8083/api/pubsub/topic?routingKey=orders.created.vn&message=ORD-1` | Gửi theo topic pattern |
| POST | `localhost:8083/api/pubsub/topic?routingKey=payments.failed.vn&message=PAY-1` | Chỉ Payment subscriber nhận |
| POST | `localhost:8083/api/pubsub/simulate` | Gửi 5 event khác nhau để quan sát routing |

---

## Dừng RabbitMQ

```bash
docker compose down
```

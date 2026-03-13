# Phân tích dự án Plugin CMS (Layered + Microkernel)

## 1. Mục tiêu hệ thống
Xây dựng một CMS dạng plugin có thể mở rộng nhanh, tắt/bật tính năng tại runtime và cung cấp một màn hình tổng quan để nhìn toàn bộ năng lực hệ thống theo thời gian thực.

Mục tiêu chính:
- Giảm phụ thuộc giữa các module nghiệp vụ.
- Thêm tính năng mới bằng plugin thay vì sửa lõi.
- Quan sát được trạng thái plugin, luồng sự kiện, workflow và schema động trên một nơi duy nhất.

## 2. Kiến trúc tổng thể
Hệ thống kết hợp:
- Layered Architecture: Presentation, Service (Microkernel Core), Domain, Infrastructure.
- Microkernel Pattern: kernel cung cấp extension points + event bus + plugin registry.

Luồng phụ thuộc:
1. Presentation gọi API tầng Service.
2. Service xử lý orchestration, kích hoạt extension points.
3. Domain giữ business rules, entity, value object, domain event.
4. Infrastructure cung cấp JPA, Redis, file storage, messaging.
5. Plugin giao tiếp với nhau qua Event Bus, không gọi trực tiếp.

## 3. Bản đồ tính năng (Feature Map)

### 3.1 Core Kernel Capabilities
- Plugin Registry: đăng ký, activate/deactivate, versioning, metadata.
- Event Bus: publish/subscribe async, retry, dead-letter.
- Extension Points: hook cho Content Lifecycle, Workflow, Schema, Search Indexing.
- Security Boundary: phân quyền plugin-level và endpoint-level.

### 3.2 Plugin Business Capabilities
- Content Editor Plugin:
  - Soạn thảo rich text, media embedding, autosave.
  - Versioning nội dung, rollback bản nháp.
- Workflow Engine Plugin:
  - Trạng thái Draft -> Review -> Approved -> Published.
  - Rule theo role (Editor, Reviewer, Publisher).
- Dynamic Schema Plugin:
  - Tạo content type động (field, validation, relation).
  - Áp schema vào form render và lưu trữ runtime.

### 3.3 Cross-Cutting Capabilities
- Audit Log: ai thay đổi gì, khi nào.
- Notification: cảnh báo khi workflow đổi trạng thái.
- Caching: tăng tốc đọc nội dung phổ biến.
- Observability: metrics plugin, event throughput, lỗi theo plugin.

## 4. Tạo hệ thống "Feature Overview"

### 4.1 Mục đích
Feature Overview System là dashboard kỹ thuật giúp team nhìn toàn cảnh chức năng theo 4 chiều:
- Trạng thái plugin.
- Độ phủ extension points.
- Luồng event runtime.
- Mức sử dụng và độ ổn định từng tính năng.

### 4.2 Mô hình dữ liệu đề xuất
- PluginSummary
  - pluginId, version, status, owner, lastActivatedAt
- FeatureCapability
  - featureId, pluginId, category, endpointCount, eventCount
- EventFlowStat
  - eventName, producerPlugin, consumerPlugin, successRate, avgLatency
- WorkflowStat
  - contentType, draftCount, reviewCount, approvedCount, publishedCount
- SchemaStat
  - contentTypeCount, dynamicFieldCount, validationErrorRate

### 4.3 API tổng quan đề xuất
- GET /api/overview/plugins
  - Trả về danh sách plugin và trạng thái runtime.
- GET /api/overview/features
  - Trả về feature map theo plugin và category.
- GET /api/overview/events
  - Trả về đồ thị producer/consumer + thông số.
- GET /api/overview/workflow
  - Trả về số liệu tồn đọng theo trạng thái phê duyệt.
- GET /api/overview/schema
  - Trả về tình trạng schema động và lỗi validate.

### 4.4 UI dashboard đề xuất
- Trang 1: System Health
  - Tổng số plugin active/inactive/error.
  - Tốc độ xử lý event, tỉ lệ lỗi trong 24h.
- Trang 2: Feature Landscape
  - Bảng plugin -> tính năng -> endpoint -> extension points.
  - Heatmap mức sử dụng theo thời gian.
- Trang 3: Event Topology
  - Biểu đồ graph plugin A phát event nào, plugin B xử lý ra sao.
  - Highlight cạnh có latency cao hoặc error spike.
- Trang 4: Workflow và Schema
  - Funnel trạng thái workflow.
  - Danh sách content type động và validation fail top.

## 5. Mapping kiến trúc với khả năng mở rộng

| Yêu cầu | Layered thuần | Layered + Microkernel |
|---|---|---|
| Thêm tính năng mới | Sửa core service | Đóng gói plugin mới |
| Triển khai tính năng | Deploy toàn hệ thống | Deploy plugin có chọn lọc |
| Cô lập lỗi | Khó cô lập module | Có thể disable plugin lỗi |
| Theo dõi tổng thể | Phân tán theo service | Hội tụ qua Overview API |

Kết luận: Layered cho tính tổ chức và tách trách nhiệm; Microkernel cho khả năng mở rộng runtime. Kết hợp hai mô hình giúp CMS vừa ổn định lõi vừa linh hoạt nghiệp vụ.

## 6. Kịch bản demo đề xuất (end-to-end)
1. Bật plugin Dynamic Schema.
2. Tạo content type mới "LandingPage" với field động.
3. Soạn nội dung bằng Content Editor.
4. Đẩy qua Workflow Engine để duyệt đa tầng.
5. Quan sát dashboard: plugin status, event flow, workflow backlog, schema stats.
6. Tắt thử Workflow plugin để chứng minh hot-swap và cô lập ảnh hưởng.

## 7. KPI đánh giá thành công
- Thời gian tích hợp plugin mới nhỏ hơn 1 ngày.
- Thời gian phát hiện plugin lỗi nhỏ hơn 5 phút qua dashboard.
- Tỉ lệ event xử lý thành công lớn hơn hoặc bằng 99.5%.
- Tỉ lệ publish đúng SLA tăng theo sprint.

## 8. Rủi ro kỹ thuật và cách giảm thiểu
- Rủi ro: plugin phụ thuộc ngầm nhau.
  - Giải pháp: bắt buộc giao tiếp qua event contract + schema versioning.
- Rủi ro: schema động gây sai lệch dữ liệu.
  - Giải pháp: validation cứng tại Domain + migration guard.
- Rủi ro: event storm khi mở rộng plugin.
  - Giải pháp: rate limit, retry policy, dead-letter queue.

## 9. Đề xuất triển khai theo giai đoạn
1. Giai đoạn 1: hoàn thiện Core Kernel + Plugin Registry + Event Bus metrics.
2. Giai đoạn 2: tích hợp 3 plugin chính (Editor, Workflow, Dynamic Schema).
3. Giai đoạn 3: xây dựng Overview API + Dashboard.
4. Giai đoạn 4: thêm cảnh báo tự động và báo cáo năng lực theo sprint.

---

Tài liệu này có thể dùng trực tiếp cho:
- Báo cáo kiến trúc môn học.
- Xây dựng backlog cho team backend/frontend.
- Làm checklist demo toàn hệ thống theo mô hình Layered + Microkernel.

package com.demo.orderservice.service;

import com.demo.orderservice.client.FoodServiceClient;
import com.demo.orderservice.client.NotificationServiceClient;
import com.demo.orderservice.client.UserServiceClient;
import com.demo.orderservice.client.VoucherServiceClient;
import com.demo.orderservice.client.dto.ApplyVoucherDto;
import com.demo.orderservice.client.dto.FoodDto;
import com.demo.orderservice.client.dto.UserDto;
import com.demo.orderservice.dto.request.OrderItemRequest;
import com.demo.orderservice.dto.request.PlaceOrderRequest;
import com.demo.orderservice.dto.response.OrderItemResponse;
import com.demo.orderservice.dto.response.OrderResponse;
import com.demo.orderservice.entity.Order;
import com.demo.orderservice.entity.OrderItem;
import com.demo.orderservice.enums.OrderStatus;
import com.demo.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal DELIVERY_FEE = BigDecimal.valueOf(15000);

    private final OrderRepository orderRepository;

    // HTTP clients đến các services khác
    private final UserServiceClient     userClient;
    private final FoodServiceClient     foodClient;
    private final VoucherServiceClient  voucherClient;
    private final NotificationServiceClient notificationClient;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        // 1. Lấy thông tin user qua HTTP
        UserDto user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + request.getUserId());
        }

        // 2. Lấy thông tin từng món và tính tổng
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            FoodDto food = foodClient.getFoodById(itemReq.getFoodId());
            if (food == null) {
                throw new RuntimeException("Không tìm thấy món ăn: " + itemReq.getFoodId());
            }
            if (!food.isAvailable()) {
                throw new RuntimeException("Món ăn hiện không có sẵn: " + food.getName());
            }
            BigDecimal itemTotal = food.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            items.add(OrderItem.builder()
                    .foodId(food.getId())
                    .foodName(food.getName())
                    .quantity(itemReq.getQuantity())
                    .price(food.getPrice())
                    .build());
        }

        // 3. Apply voucher qua HTTP (validate + increment usage trong 1 request)
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long voucherId = null;
        String voucherCode = null;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            ApplyVoucherDto applyResult = voucherClient.applyVoucher(
                    request.getVoucherCode(), totalAmount);
            if (!applyResult.isValid()) {
                throw new RuntimeException(applyResult.getMessage());
            }
            discountAmount = applyResult.getDiscountAmount();
            voucherId = applyResult.getVoucherId();
            voucherCode = request.getVoucherCode();
        }

        BigDecimal finalAmount = totalAmount.add(DELIVERY_FEE).subtract(discountAmount);

        // 4. Tạo order
        Order order = Order.builder()
                .userId(user.getId())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .deliveryFee(DELIVERY_FEE)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .voucherId(voucherId)
                .voucherCode(voucherCode)
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order saved = orderRepository.save(order);

        // 5. Tăng orderCount (best-effort)
        for (OrderItemRequest itemReq : request.getItems()) {
            foodClient.incrementOrderCount(itemReq.getFoodId(), itemReq.getQuantity());
        }

        // 6. Gửi notification qua HTTP (best-effort)
        notificationClient.send(user.getId(),
                "Đơn hàng #" + saved.getId() + " đã đặt thành công. Đang chờ xác nhận.",
                saved.getId());

        return toResponse(saved, user.getName());
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new RuntimeException(
                    "Không thể chuyển trạng thái từ " + order.getStatus() + " sang " + newStatus);
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        notificationClient.send(order.getUserId(),
                buildStatusMessage(orderId, newStatus), orderId);

        return toResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> toResponse(o, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> toResponse(o, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(o -> toResponse(o, null))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
    }

    private String buildStatusMessage(Long orderId, OrderStatus status) {
        return switch (status) {
            case CONFIRMED  -> "Đơn hàng #" + orderId + " đã được xác nhận!";
            case PREPARING  -> "Đơn hàng #" + orderId + " đang được chuẩn bị.";
            case DELIVERING -> "Đơn hàng #" + orderId + " đang trên đường giao đến bạn!";
            case COMPLETED  -> "Đơn hàng #" + orderId + " đã giao thành công. Cảm ơn bạn!";
            case CANCELLED  -> "Đơn hàng #" + orderId + " đã bị huỷ.";
            default -> "Đơn hàng #" + orderId + " cập nhật trạng thái: " + status;
        };
    }

    private OrderResponse toResponse(Order order, String userName) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .foodId(item.getFoodId())
                        .foodName(item.getFoodName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .userName(userName)
                .items(itemResponses)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .voucherCode(order.getVoucherCode())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

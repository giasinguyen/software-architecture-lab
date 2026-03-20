package com.iuh.fit.service;

import com.iuh.fit.dto.request.OrderItemRequest;
import com.iuh.fit.dto.request.PlaceOrderRequest;
import com.iuh.fit.dto.response.OrderItemResponse;
import com.iuh.fit.dto.response.OrderResponse;
import com.iuh.fit.entity.*;
import com.iuh.fit.enums.OrderStatus;
import com.iuh.fit.repository.FoodRepository;
import com.iuh.fit.repository.OrderRepository;
import com.iuh.fit.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final VoucherService voucherService;
    private final NotificationService notificationService;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + request.getUserId()));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Food food = foodRepository.findById(itemReq.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn: " + itemReq.getFoodId()));
            if (!food.isAvailable()) {
                throw new RuntimeException("Món ăn hiện không có sẵn: " + food.getName());
            }
            BigDecimal itemTotal = food.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            items.add(OrderItem.builder()
                    .food(food)
                    .quantity(itemReq.getQuantity())
                    .price(food.getPrice())
                    .build());
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher appliedVoucher = null;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            Voucher voucher = voucherService.findValidVoucher(request.getVoucherCode())
                    .orElseThrow(() -> new RuntimeException("Voucher không hợp lệ: " + request.getVoucherCode()));
            discountAmount = voucherService.calculateDiscount(voucher, totalAmount);
            voucherService.incrementUsage(voucher);
            appliedVoucher = voucher;
        }

        BigDecimal finalAmount = totalAmount.add(DELIVERY_FEE).subtract(discountAmount);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .deliveryFee(DELIVERY_FEE)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .voucher(appliedVoucher)
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
            Food food = item.getFood();
            food.setOrderCount(food.getOrderCount() + item.getQuantity());
            foodRepository.save(food);
        }

        Order saved = orderRepository.save(order);
        notificationService.send(user,
                "Đơn hàng #" + saved.getId() + " đã đặt thành công. Đang chờ xác nhận.",
                saved.getId());
        return toResponse(saved);
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
        notificationService.send(order.getUser(), buildStatusMessage(orderId, newStatus), orderId);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));
        return toResponse(order);
    }

    private String buildStatusMessage(Long orderId, OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Đơn hàng #" + orderId + " đã được xác nhận.";
            case PREPARING -> "Đơn hàng #" + orderId + " đang được chuẩn bị.";
            case DELIVERING -> "Shipper đang giao đơn hàng #" + orderId + " đến bạn.";
            case COMPLETED -> "Đơn hàng #" + orderId + " đã giao thành công. Cảm ơn bạn!";
            case CANCELLED -> "Đơn hàng #" + orderId + " đã bị hủy.";
            default -> "Trạng thái đơn hàng #" + orderId + " đã thay đổi.";
        };
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .foodId(item.getFood().getId())
                        .foodName(item.getFood().getName())
                        .foodImageUrl(item.getFood().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .items(itemResponses)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

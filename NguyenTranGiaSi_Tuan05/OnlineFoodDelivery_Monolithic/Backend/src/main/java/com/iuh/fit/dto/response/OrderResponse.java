package com.iuh.fit.dto.response;

import com.iuh.fit.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String voucherCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.demo.orderservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long foodId;
    private String foodName;
    private int quantity;
    private BigDecimal price;
}

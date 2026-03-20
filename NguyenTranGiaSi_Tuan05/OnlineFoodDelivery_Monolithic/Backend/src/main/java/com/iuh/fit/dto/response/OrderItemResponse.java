package com.iuh.fit.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private String foodImageUrl;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}

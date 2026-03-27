package com.demo.orderservice.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long foodId;
    private int quantity;
}

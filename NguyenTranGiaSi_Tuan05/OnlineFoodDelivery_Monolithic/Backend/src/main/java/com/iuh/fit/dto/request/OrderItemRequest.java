package com.iuh.fit.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long foodId;
    private int quantity;
}

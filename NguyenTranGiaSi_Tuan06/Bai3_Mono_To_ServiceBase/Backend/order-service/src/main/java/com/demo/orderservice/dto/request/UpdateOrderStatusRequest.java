package com.demo.orderservice.dto.request;

import com.demo.orderservice.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}

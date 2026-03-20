package com.iuh.fit.dto.request;

import com.iuh.fit.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}

package com.demo.orderservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private String voucherCode;
}

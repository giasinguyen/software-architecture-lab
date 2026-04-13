package com.example.order.event;

import lombok.*;

@Getter @AllArgsConstructor @ToString
public class OrderEvent {
    private final String type; // ORDER_CREATED, ORDER_CANCELLED
    private final Long orderId;
    private final String customerName;
    private final String product;
    private final int quantity;
    private final double totalPrice;
    private final String status;
}

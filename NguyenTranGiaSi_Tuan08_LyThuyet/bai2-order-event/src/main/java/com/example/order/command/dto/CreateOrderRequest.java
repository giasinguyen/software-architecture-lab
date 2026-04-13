package com.example.order.command.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateOrderRequest {
    private String customerName;
    private String product;
    private int quantity;
    private double totalPrice;
}

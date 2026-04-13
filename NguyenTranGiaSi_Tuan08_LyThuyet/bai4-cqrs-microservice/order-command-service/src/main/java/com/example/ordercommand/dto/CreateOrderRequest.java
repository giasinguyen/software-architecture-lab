package com.example.ordercommand.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateOrderRequest {
    private String customerName;
    private String product;
    private int quantity;
    private double totalPrice;
}

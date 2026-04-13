package com.example.ordercommand.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderEventDto {
    private String type;
    private Long orderId;
    private String customerName;
    private String product;
    private int quantity;
    private double totalPrice;
    private String status;
}

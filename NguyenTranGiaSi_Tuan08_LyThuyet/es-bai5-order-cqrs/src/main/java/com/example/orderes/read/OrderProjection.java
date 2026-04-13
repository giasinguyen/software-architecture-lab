package com.example.orderes.read;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_projection")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderProjection {
    @Id
    private String orderId;
    private String customerName;
    private String status;
    private double totalPrice;
    private int itemCount;

    @Column(columnDefinition = "TEXT")
    private String items; // JSON-like string of items
}

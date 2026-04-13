package com.example.orderes.write;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String eventType; // ORDER_CREATED, ITEM_ADDED, ITEM_REMOVED, ORDER_CONFIRMED
    private String itemName;
    private double itemPrice;
    private int quantity;
    private LocalDateTime createdAt;
}

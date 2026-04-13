package com.example.orderquery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_view")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderView {
    @Id
    private Long id;
    private String customerName;
    private String product;
    private int quantity;
    private double totalPrice;
    private String status;
}

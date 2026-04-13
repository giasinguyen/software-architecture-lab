package com.example.projection.write;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountId;
    private String eventType;
    private double amount;
    private LocalDateTime createdAt;
}

package com.example.bank.event;

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
    private String eventType; // ACCOUNT_CREATED, MONEY_DEPOSITED, MONEY_WITHDRAWN
    private double amount;
    private LocalDateTime createdAt;
}

package com.example.snapshot.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_snapshots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountId;
    private double balance;
    private Long lastEventId; // ID of the last event included in this snapshot
    private LocalDateTime createdAt;
}

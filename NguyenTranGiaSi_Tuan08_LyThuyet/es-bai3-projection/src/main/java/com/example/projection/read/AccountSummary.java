package com.example.projection.read;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountSummary {
    @Id
    private String accountId;
    private double balance;
    private double totalDeposits;
    private double totalWithdrawals;
    private int transactionCount;
}

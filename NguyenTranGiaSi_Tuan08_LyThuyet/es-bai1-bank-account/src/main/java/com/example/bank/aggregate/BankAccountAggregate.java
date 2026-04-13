package com.example.bank.aggregate;

import com.example.bank.event.AccountEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class BankAccountAggregate {
    private String accountId;
    private double balance;
    private String ownerName;

    public BankAccountAggregate() {
        this.balance = 0;
    }

    public void apply(AccountEvent event) {
        switch (event.getEventType()) {
            case "ACCOUNT_CREATED" -> {
                this.accountId = event.getAccountId();
                this.balance = event.getAmount(); // initial deposit
            }
            case "MONEY_DEPOSITED" -> this.balance += event.getAmount();
            case "MONEY_WITHDRAWN" -> this.balance -= event.getAmount();
        }
    }

    public static BankAccountAggregate replay(List<AccountEvent> events) {
        BankAccountAggregate aggregate = new BankAccountAggregate();
        for (AccountEvent event : events) {
            aggregate.apply(event);
        }
        return aggregate;
    }
}

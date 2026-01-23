package org.example.strategy;

public class CashPayment implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Thanh toán tiền mặt: " + amount);
    }
}

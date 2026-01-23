package org.example.strategy;

public class CardPayment implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Thanh toán thẻ: " + amount);
    }
}

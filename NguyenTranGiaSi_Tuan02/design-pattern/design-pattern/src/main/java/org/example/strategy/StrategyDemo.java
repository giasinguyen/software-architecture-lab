package org.example.strategy;

public class StrategyDemo {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();

        context.setStrategy(new CashPayment());
        context.executePayment(50000);

        context.setStrategy(new CardPayment());
        context.executePayment(100000);
    }
}

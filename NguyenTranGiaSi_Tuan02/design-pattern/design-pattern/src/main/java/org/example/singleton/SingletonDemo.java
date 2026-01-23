package org.example.singleton;

public class SingletonDemo {
    public static void main(String[] args) {
        OrderManager om1 = OrderManager.getInstance();
        OrderManager om2 = OrderManager.getInstance();

        om1.createOrder("Coffee");
        System.out.println(om1 == om2);
    }
}

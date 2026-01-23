package org.example.state;

public class StateDemo {
    public static void main(String[] args) {
        Order order = new Order();
        order.process();
        order.process();
        order.process();
    }
}

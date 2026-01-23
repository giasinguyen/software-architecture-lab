package org.example.state;

public class Order {
    private OrderState state;

    public Order() {
        state = new NewOrderState();
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void process() {
        state.handle(this);
    }
}

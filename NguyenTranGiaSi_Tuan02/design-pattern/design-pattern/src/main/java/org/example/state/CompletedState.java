package org.example.state;

public class CompletedState implements OrderState {
    @Override
    public void handle(Order order) {
        System.out.println("Đơn hàng đã hoàn thành");
    }
}

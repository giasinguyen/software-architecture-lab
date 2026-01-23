package org.example.state;

public class ProcessingState implements OrderState {
    @Override
    public void handle(Order order) {
        System.out.println("Đơn hàng đang được xử lý");
        order.setState(new CompletedState());
    }
}

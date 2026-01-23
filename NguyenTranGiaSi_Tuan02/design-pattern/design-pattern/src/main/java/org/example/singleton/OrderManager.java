package org.example.singleton;

public class OrderManager {
    private static OrderManager instance;

    private OrderManager() {}

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void createOrder(String orderName) {
        System.out.println("Tạo đơn hàng: " + orderName);
    }
}


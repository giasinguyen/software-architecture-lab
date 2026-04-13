package com.example.orderes.read;

import com.example.orderes.write.OrderEvent;
import com.example.orderes.write.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderProjectionService {

    private final OrderEventRepository eventRepository;
    private final OrderProjectionRepository projectionRepository;

    public void rebuildProjection(String orderId) {
        List<OrderEvent> events = eventRepository.findByOrderIdOrderByIdAsc(orderId);
        if (events.isEmpty()) return;

        String customerName = "";
        String status = "CREATED";
        Map<String, ItemInfo> items = new LinkedHashMap<>();

        for (OrderEvent e : events) {
            switch (e.getEventType()) {
                case "ORDER_CREATED" -> { customerName = e.getItemName(); status = "CREATED"; }
                case "ITEM_ADDED" -> items.put(e.getItemName(),
                        new ItemInfo(e.getItemName(), e.getItemPrice(), e.getQuantity()));
                case "ITEM_REMOVED" -> items.remove(e.getItemName());
                case "ORDER_CONFIRMED" -> status = "CONFIRMED";
            }
        }

        double totalPrice = items.values().stream()
                .mapToDouble(i -> i.price * i.quantity).sum();

        projectionRepository.save(OrderProjection.builder()
                .orderId(orderId)
                .customerName(customerName)
                .status(status)
                .totalPrice(totalPrice)
                .itemCount(items.size())
                .items(items.values().toString())
                .build());
    }

    public Map<String, Object> rebuildAndReturn(String orderId) {
        rebuildProjection(orderId);
        OrderProjection p = projectionRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return Map.of("orderId", p.getOrderId(), "customerName", p.getCustomerName(),
                "status", p.getStatus(), "totalPrice", p.getTotalPrice(),
                "itemCount", p.getItemCount(), "items", p.getItems());
    }

    public OrderProjection getProjection(String orderId) {
        return projectionRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public List<OrderProjection> getAll() {
        return projectionRepository.findAll();
    }

    record ItemInfo(String name, double price, int quantity) {
        @Override
        public String toString() {
            return name + " x" + quantity + " @" + price;
        }
    }
}

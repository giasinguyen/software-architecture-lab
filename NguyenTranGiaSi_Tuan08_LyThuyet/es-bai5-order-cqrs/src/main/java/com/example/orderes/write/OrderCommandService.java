package com.example.orderes.write;

import com.example.orderes.read.OrderProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderEventRepository eventRepository;
    private final OrderProjectionService projectionService;

    @Transactional
    public Map<String, Object> createOrder(String customerName) {
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        eventRepository.save(OrderEvent.builder()
                .orderId(orderId).eventType("ORDER_CREATED")
                .itemName(customerName).itemPrice(0).quantity(0)
                .createdAt(LocalDateTime.now()).build());
        projectionService.rebuildProjection(orderId);
        return Map.of("orderId", orderId, "customerName", customerName, "status", "CREATED");
    }

    @Transactional
    public Map<String, Object> addItem(String orderId, String itemName, double price, int quantity) {
        eventRepository.save(OrderEvent.builder()
                .orderId(orderId).eventType("ITEM_ADDED")
                .itemName(itemName).itemPrice(price).quantity(quantity)
                .createdAt(LocalDateTime.now()).build());
        return projectionService.rebuildAndReturn(orderId);
    }

    @Transactional
    public Map<String, Object> removeItem(String orderId, String itemName) {
        eventRepository.save(OrderEvent.builder()
                .orderId(orderId).eventType("ITEM_REMOVED")
                .itemName(itemName).itemPrice(0).quantity(0)
                .createdAt(LocalDateTime.now()).build());
        return projectionService.rebuildAndReturn(orderId);
    }

    @Transactional
    public Map<String, Object> confirmOrder(String orderId) {
        eventRepository.save(OrderEvent.builder()
                .orderId(orderId).eventType("ORDER_CONFIRMED")
                .createdAt(LocalDateTime.now()).build());
        return projectionService.rebuildAndReturn(orderId);
    }
}

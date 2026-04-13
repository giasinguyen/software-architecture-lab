package com.example.ordercommand.service;

import com.example.ordercommand.dto.CreateOrderRequest;
import com.example.ordercommand.dto.OrderEventDto;
import com.example.ordercommand.event.EventBus;
import com.example.ordercommand.model.OrderEntity;
import com.example.ordercommand.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final EventBus eventBus;

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .customerName(request.getCustomerName())
                .product(request.getProduct())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        eventBus.publish(new OrderEventDto("ORDER_CREATED", order.getId(),
                order.getCustomerName(), order.getProduct(),
                order.getQuantity(), order.getTotalPrice(), order.getStatus()));
        return order;
    }

    @Transactional
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        order.setStatus("CANCELLED");
        order = orderRepository.save(order);

        eventBus.publish(new OrderEventDto("ORDER_CANCELLED", order.getId(),
                order.getCustomerName(), order.getProduct(),
                order.getQuantity(), order.getTotalPrice(), order.getStatus()));
        return order;
    }
}

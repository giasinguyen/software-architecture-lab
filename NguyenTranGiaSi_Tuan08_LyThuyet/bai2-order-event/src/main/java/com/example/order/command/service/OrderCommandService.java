package com.example.order.command.service;

import com.example.order.command.dto.CreateOrderRequest;
import com.example.order.command.model.OrderEntity;
import com.example.order.command.model.OrderStatus;
import com.example.order.command.repository.OrderWriteRepository;
import com.example.order.event.EventPublisher;
import com.example.order.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderWriteRepository writeRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .customerName(request.getCustomerName())
                .product(request.getProduct())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        order = writeRepository.save(order);

        eventPublisher.publish(new OrderEvent(
                "ORDER_CREATED", order.getId(), order.getCustomerName(),
                order.getProduct(), order.getQuantity(), order.getTotalPrice(),
                order.getStatus().name()));
        return order;
    }

    @Transactional
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order = writeRepository.save(order);

        eventPublisher.publish(new OrderEvent(
                "ORDER_CANCELLED", order.getId(), order.getCustomerName(),
                order.getProduct(), order.getQuantity(), order.getTotalPrice(),
                order.getStatus().name()));
        return order;
    }
}

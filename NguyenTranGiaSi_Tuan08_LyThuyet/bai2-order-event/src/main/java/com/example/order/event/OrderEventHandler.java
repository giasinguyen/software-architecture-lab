package com.example.order.event;

import com.example.order.query.model.OrderView;
import com.example.order.query.repository.OrderReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final OrderReadRepository readRepository;

    @EventListener
    public void handle(OrderEvent event) {
        log.info("Received event: {}", event);

        OrderView view = OrderView.builder()
                .id(event.getOrderId())
                .customerName(event.getCustomerName())
                .product(event.getProduct())
                .quantity(event.getQuantity())
                .totalPrice(event.getTotalPrice())
                .status(event.getStatus())
                .build();
        readRepository.save(view);
        log.info("Read model updated for order: {}", event.getOrderId());
    }
}

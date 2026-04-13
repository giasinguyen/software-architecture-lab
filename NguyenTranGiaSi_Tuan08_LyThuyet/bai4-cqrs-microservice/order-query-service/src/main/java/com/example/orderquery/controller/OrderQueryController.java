package com.example.orderquery.controller;

import com.example.orderquery.dto.OrderEventDto;
import com.example.orderquery.model.OrderView;
import com.example.orderquery.repository.OrderViewRepository;
import com.example.orderquery.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderQueryController {

    private final OrderQueryService queryService;
    private final OrderViewRepository repository;

    // ===== QUERY endpoints =====

    @GetMapping("/orders")
    public ResponseEntity<List<OrderView>> getAll() {
        return ResponseEntity.ok(queryService.getAll());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderView> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }

    // ===== Event receiver (simulated message queue) =====

    @PostMapping("/events")
    public ResponseEntity<Void> receiveEvent(@RequestBody OrderEventDto event) {
        log.info("Received event: {} for order {}", event.getType(), event.getOrderId());

        OrderView view = OrderView.builder()
                .id(event.getOrderId())
                .customerName(event.getCustomerName())
                .product(event.getProduct())
                .quantity(event.getQuantity())
                .totalPrice(event.getTotalPrice())
                .status(event.getStatus())
                .build();
        repository.save(view);

        log.info("Read model updated for order: {}", event.getOrderId());
        return ResponseEntity.ok().build();
    }
}

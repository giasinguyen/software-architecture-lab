package com.example.order.controller;

import com.example.order.command.dto.CreateOrderRequest;
import com.example.order.command.model.OrderEntity;
import com.example.order.command.service.OrderCommandService;
import com.example.order.query.model.OrderView;
import com.example.order.query.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    // ===== COMMAND =====

    @PostMapping
    public ResponseEntity<OrderEntity> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(commandService.createOrder(request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderEntity> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.cancelOrder(id));
    }

    // ===== QUERY =====

    @GetMapping
    public ResponseEntity<List<OrderView>> getAll() {
        return ResponseEntity.ok(queryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }
}

package com.example.ordercommand.controller;

import com.example.ordercommand.dto.CreateOrderRequest;
import com.example.ordercommand.model.OrderEntity;
import com.example.ordercommand.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService commandService;

    @PostMapping
    public ResponseEntity<OrderEntity> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(commandService.createOrder(request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderEntity> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.cancelOrder(id));
    }
}

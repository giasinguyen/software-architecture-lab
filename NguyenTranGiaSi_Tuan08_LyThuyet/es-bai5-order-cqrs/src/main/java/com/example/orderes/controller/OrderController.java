package com.example.orderes.controller;

import com.example.orderes.read.OrderProjection;
import com.example.orderes.read.OrderProjectionService;
import com.example.orderes.write.OrderCommandService;
import com.example.orderes.write.OrderEvent;
import com.example.orderes.write.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderProjectionService projectionService;
    private final OrderEventRepository eventRepository;

    // ===== COMMAND (Write) =====

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(commandService.createOrder(body.get("customerName")));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<?> addItem(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(commandService.addItem(id,
                (String) body.get("itemName"),
                ((Number) body.get("price")).doubleValue(),
                ((Number) body.get("quantity")).intValue()));
    }

    @DeleteMapping("/{id}/items/{itemName}")
    public ResponseEntity<?> removeItem(@PathVariable String id, @PathVariable String itemName) {
        return ResponseEntity.ok(commandService.removeItem(id, itemName));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable String id) {
        return ResponseEntity.ok(commandService.confirmOrder(id));
    }

    // ===== QUERY (Read - Projection) =====

    @GetMapping("/{id}")
    public ResponseEntity<OrderProjection> get(@PathVariable String id) {
        return ResponseEntity.ok(projectionService.getProjection(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderProjection>> getAll() {
        return ResponseEntity.ok(projectionService.getAll());
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<OrderEvent>> getEvents(@PathVariable String id) {
        return ResponseEntity.ok(eventRepository.findByOrderIdOrderByIdAsc(id));
    }
}

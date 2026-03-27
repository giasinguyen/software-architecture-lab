package com.demo.functional.controller;

import com.demo.functional.service.FunctionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/functional")
@RequiredArgsConstructor
public class FunctionalController {

    private final FunctionalService functionalService;

    // GET /api/functional/users  – lấy dữ liệu từ user_db
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(functionalService.getAllUsers());
    }

    // GET /api/functional/orders  – lấy dữ liệu từ order_db
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        return ResponseEntity.ok(functionalService.getAllOrders());
    }

    // GET /api/functional/benchmark
    @GetMapping("/benchmark")
    public Map<String, Object> benchmark() {
        return functionalService.benchmark();
    }
}

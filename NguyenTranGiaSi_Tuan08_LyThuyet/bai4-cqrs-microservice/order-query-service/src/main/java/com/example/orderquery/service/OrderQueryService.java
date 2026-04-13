package com.example.orderquery.service;

import com.example.orderquery.model.OrderView;
import com.example.orderquery.repository.OrderViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderViewRepository repository;

    public List<OrderView> getAll() {
        return repository.findAll();
    }

    public OrderView getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}

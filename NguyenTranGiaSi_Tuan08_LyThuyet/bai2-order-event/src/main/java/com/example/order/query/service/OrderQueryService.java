package com.example.order.query.service;

import com.example.order.query.model.OrderView;
import com.example.order.query.repository.OrderReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderReadRepository readRepository;

    public List<OrderView> getAll() {
        return readRepository.findAll();
    }

    public OrderView getById(Long id) {
        return readRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}

package com.iuh.fit.repository;

import com.iuh.fit.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository cho Order - Technical Partition
 * Tất cả các repository được đặt trong package repository
 */
@Repository
public class OrderRepository {
    
    private final Map<Long, Order> orders = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
    
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }
    
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
    
    public List<Order> findByUserId(Long userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public void deleteById(Long id) {
        orders.remove(id);
    }
    
    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }
}

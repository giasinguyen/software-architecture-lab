package com.example.order.query.repository;

import com.example.order.query.model.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReadRepository extends JpaRepository<OrderView, Long> {
}

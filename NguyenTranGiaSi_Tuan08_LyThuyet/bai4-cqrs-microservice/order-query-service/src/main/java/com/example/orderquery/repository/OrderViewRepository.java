package com.example.orderquery.repository;

import com.example.orderquery.model.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderViewRepository extends JpaRepository<OrderView, Long> {
}

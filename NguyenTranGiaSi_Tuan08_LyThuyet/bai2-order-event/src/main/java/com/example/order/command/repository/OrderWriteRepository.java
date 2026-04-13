package com.example.order.command.repository;

import com.example.order.command.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderWriteRepository extends JpaRepository<OrderEntity, Long> {
}

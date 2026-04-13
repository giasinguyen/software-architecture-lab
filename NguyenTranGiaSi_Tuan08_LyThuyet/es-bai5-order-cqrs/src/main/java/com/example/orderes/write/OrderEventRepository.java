package com.example.orderes.write;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    List<OrderEvent> findByOrderIdOrderByIdAsc(String orderId);
}

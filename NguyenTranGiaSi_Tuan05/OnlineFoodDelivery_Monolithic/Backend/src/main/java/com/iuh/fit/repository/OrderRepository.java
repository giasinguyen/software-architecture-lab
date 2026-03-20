package com.iuh.fit.repository;

import com.iuh.fit.entity.Order;
import com.iuh.fit.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    @Query("SELECT oi.food.id FROM OrderItem oi WHERE oi.order.user.id = :userId GROUP BY oi.food.id ORDER BY COUNT(oi) DESC")
    List<Long> findFoodIdsByUserOrderHistory(@Param("userId") Long userId);
}

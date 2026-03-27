package com.demo.foodservice.repository;

import com.demo.foodservice.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByAvailableTrue();

    @Query("SELECT f FROM Food f WHERE f.available = true ORDER BY f.orderCount DESC")
    List<Food> findTopByOrderCount(Pageable pageable);

    // Query trực tiếp vào shared DB để lấy food ids từ order history của user
    // Service-based architecture: shared database nên có thể cross-query
    @Query(value = """
            SELECT DISTINCT oi.food_id
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.id
            WHERE o.user_id = :userId
            ORDER BY oi.food_id
            """, nativeQuery = true)
    List<Long> findFoodIdsByUserOrderHistory(@Param("userId") Long userId);
}

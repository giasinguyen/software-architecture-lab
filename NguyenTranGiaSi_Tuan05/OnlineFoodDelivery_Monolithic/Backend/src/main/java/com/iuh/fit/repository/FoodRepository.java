package com.iuh.fit.repository;

import com.iuh.fit.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByAvailableTrue();

    List<Food> findByCategoryAndAvailableTrue(String category);

    @Query("SELECT f FROM Food f WHERE f.available = true ORDER BY f.orderCount DESC")
    List<Food> findTopByOrderCount(Pageable pageable);
}

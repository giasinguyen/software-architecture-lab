package com.demo.search.repository;

import com.demo.search.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Phương án 2 – Backend search.
     * Spring Data JPA tự sinh câu LIKE query dựa vào tên method.
     * Tương đương: WHERE LOWER(name) LIKE %kw%
     *           OR LOWER(description) LIKE %kw%
     *           OR LOWER(category) LIKE %kw%
     */
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String description, String category);
}

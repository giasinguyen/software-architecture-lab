package com.iuh.fit.repository;

import com.iuh.fit.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {

    public List<Product> findAll() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return List.of(
                new Product(1L, "iPhone", 1200),
                new Product(2L, "MacBook", 2500),
                new Product(3L, "AirPods", 250)
        );
    }
}

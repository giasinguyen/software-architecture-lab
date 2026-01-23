package com.iuh.fit.controller;

import com.iuh.fit.entity.Product;
import com.iuh.fit.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> getAll() {
        long start = System.currentTimeMillis();

        List<Product> products = service.getAllProducts();

        long end = System.currentTimeMillis();

        return Map.of(
                "executionTimeMs", (end - start),
                "data", products
        );
    }
}


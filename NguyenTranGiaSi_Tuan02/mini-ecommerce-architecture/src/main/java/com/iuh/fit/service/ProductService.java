package com.iuh.fit.service;

import com.iuh.fit.entity.Product;
import com.iuh.fit.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
}

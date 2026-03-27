package com.demo.search.service;

import com.demo.search.entity.Product;
import com.demo.search.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ──────────────────────────────────────────────────────────────────────────
    // Phương án 1: Trả TOÀN BỘ dữ liệu về FE → FE tự filter (client-side)
    // ──────────────────────────────────────────────────────────────────────────
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Phương án 2: FE gửi keyword → BE thực hiện LIKE query qua JPA
    // ──────────────────────────────────────────────────────────────────────────
    public List<Product> searchBackend(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                        keyword, keyword, keyword);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Phương án 3: FE gửi keyword → BE gọi stored procedure trong DB
    // ──────────────────────────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public List<Product> searchStoredProcedure(String keyword) {
        // Dùng @NamedStoredProcedureQuery đã khai báo trên entity Product
        var query = entityManager.createNamedStoredProcedureQuery("Product.spSearch");
        query.setParameter("p_keyword", keyword == null ? "" : keyword);
        return query.getResultList();
    }
}

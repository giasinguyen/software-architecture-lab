package com.demo.search.controller;

import com.demo.search.entity.Product;
import com.demo.search.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Phương án 1 – Client-side search
     * FE tải toàn bộ, tự filter bằng JS
     */
    @GetMapping("/all")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    /**
     * Phương án 2 – Backend search
     * BE thực hiện LIKE query qua Spring Data JPA
     */
    @GetMapping("/search")
    public List<Product> search(@RequestParam(defaultValue = "") String q) {
        return productService.searchBackend(q);
    }

    /**
     * Phương án 3 – Stored procedure
     * BE gọi CALL sp_search_products(?) trong MariaDB
     */
    @GetMapping("/search-sp")
    public List<Product> searchSP(@RequestParam(defaultValue = "") String q) {
        return productService.searchStoredProcedure(q);
    }
}

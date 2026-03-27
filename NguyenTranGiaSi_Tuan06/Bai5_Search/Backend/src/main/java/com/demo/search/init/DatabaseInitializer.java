package com.demo.search.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Tự động tạo stored procedure và seed dữ liệu mẫu khi ứng dụng khởi động.
 * Dùng ApplicationReadyEvent để đảm bảo JPA đã tạo bảng xong (ddl-auto: update).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        createStoredProcedure();
        seedDataIfEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stored procedure: tìm kiếm LIKE trên 3 cột name, description, category
    // ─────────────────────────────────────────────────────────────────────────
    private void createStoredProcedure() {
        try {
            jdbcTemplate.execute("DROP PROCEDURE IF EXISTS sp_search_products");
            jdbcTemplate.execute("""
                    CREATE PROCEDURE sp_search_products(IN p_keyword VARCHAR(255))
                    BEGIN
                        SELECT * FROM products
                        WHERE name        LIKE CONCAT('%', p_keyword, '%')
                           OR description LIKE CONCAT('%', p_keyword, '%')
                           OR category    LIKE CONCAT('%', p_keyword, '%');
                    END
                    """);
            log.info("Stored procedure sp_search_products created successfully");
        } catch (Exception e) {
            log.error("Failed to create stored procedure: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Seed 20 sản phẩm mẫu nếu bảng đang trống
    // ─────────────────────────────────────────────────────────────────────────
    private void seedDataIfEmpty() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        if (count != null && count > 0) return;

        jdbcTemplate.batchUpdate("""
                INSERT INTO products (name, category, price, description) VALUES (?, ?, ?, ?)
                """,
                java.util.List.of(
                        new Object[]{"iPhone 15 Pro", "Electronics", 999.99, "Apple smartphone with A17 Pro chip"},
                        new Object[]{"Samsung Galaxy S24", "Electronics", 899.00, "Samsung flagship with Snapdragon 8 Gen 3"},
                        new Object[]{"MacBook Air M3", "Electronics", 1299.00, "Ultra-thin laptop with Apple Silicon"},
                        new Object[]{"Sony WH-1000XM5", "Electronics", 349.99, "Wireless noise-cancelling headphones"},
                        new Object[]{"iPad Pro 12.9", "Electronics", 1099.00, "Professional tablet with M2 chip"},
                        new Object[]{"Organic Green Tea", "Food", 12.50, "Premium Japanese matcha green tea leaves"},
                        new Object[]{"Dark Roast Coffee", "Food", 18.99, "Single-origin Arabica dark roast blend"},
                        new Object[]{"Protein Bar Variety Pack", "Food", 29.99, "High-protein snack bars, 12 flavors"},
                        new Object[]{"Himalayan Pink Salt", "Food", 7.50, "Natural mineral salt for cooking"},
                        new Object[]{"Almond Butter", "Food", 14.99, "Natural creamy almond butter, no additives"},
                        new Object[]{"Levi's 501 Jeans", "Clothing", 59.99, "Classic straight-fit denim jeans"},
                        new Object[]{"Nike Air Max 270", "Clothing", 149.00, "Running shoes with Max Air cushioning"},
                        new Object[]{"Uniqlo Fleece Jacket", "Clothing", 49.90, "Lightweight and warm fleece zip-up"},
                        new Object[]{"Adidas Ultraboost 23", "Clothing", 180.00, "Performance running shoes with Boost midsole"},
                        new Object[]{"Wool Beanie Hat", "Clothing", 22.99, "100% merino wool winter hat, multiple colors"},
                        new Object[]{"Clean Code (Book)", "Books", 35.00, "Robert C. Martin – software craftsmanship guide"},
                        new Object[]{"Designing Data-Intensive Apps", "Books", 45.00, "Martin Kleppmann – distributed systems primer"},
                        new Object[]{"The Pragmatic Programmer", "Books", 40.00, "Hunt & Thomas – career guide for developers"},
                        new Object[]{"Spring Boot in Action", "Books", 38.00, "Craig Walls – hands-on Spring Boot guide"},
                        new Object[]{"Docker Deep Dive", "Books", 29.99, "Nigel Poulton – containers from zero to hero"}
                )
        );
        log.info("Seeded 20 sample products");
    }
}

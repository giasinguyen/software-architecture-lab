package com.demo.functional.service;

import com.demo.functional.entity.order.Order;
import com.demo.functional.entity.user.User;
import com.demo.functional.repository.order.OrderRepository;
import com.demo.functional.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionalService {

    // Spring inject đúng datasource nhờ @EnableJpaRepositories trong config
    private final UserRepository  userRepo;
    private final OrderRepository orderRepo;

    public List<User>  getAllUsers()  { return userRepo.findAll(); }
    public List<Order> getAllOrders() { return orderRepo.findAll(); }

    // ── BENCHMARK: 2 datasource hoạt động song song, độc lập ──
    public Map<String, Object> benchmark() {
        Map<String, Object> result = new HashMap<>();

        // Query user_db
        long t1 = System.currentTimeMillis();
        long userCount = userRepo.count();
        long t2 = System.currentTimeMillis();

        // Query order_db (datasource hoàn toàn riêng)
        long orderCount = orderRepo.count();
        long t3 = System.currentTimeMillis();

        result.put("user_db_rows",       userCount);
        result.put("user_db_query_ms",   t2 - t1);
        result.put("order_db_rows",      orderCount);
        result.put("order_db_query_ms",  t3 - t2);
        result.put("note", "2 datasource độc lập → connection pool riêng → không tranh giành tài nguyên");

        log.info("Benchmark Functional: user_db={}ms, order_db={}ms", t2 - t1, t3 - t2);
        return result;
    }
}

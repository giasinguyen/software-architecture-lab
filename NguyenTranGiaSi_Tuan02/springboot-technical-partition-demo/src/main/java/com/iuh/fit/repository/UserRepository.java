package com.iuh.fit.repository;

import com.iuh.fit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository cho User - Technical Partition
 * Tất cả các repository được đặt trong package repository
 */
@Repository
public class UserRepository {
    
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    public void deleteById(Long id) {
        users.remove(id);
    }
    
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}

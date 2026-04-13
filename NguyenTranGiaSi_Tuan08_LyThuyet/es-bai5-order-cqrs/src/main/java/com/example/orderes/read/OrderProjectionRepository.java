package com.example.orderes.read;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProjectionRepository extends JpaRepository<OrderProjection, String> {
}

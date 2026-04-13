package com.example.todo.query.repository;

import com.example.todo.query.model.TodoView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoReadRepository extends JpaRepository<TodoView, Long> {
}

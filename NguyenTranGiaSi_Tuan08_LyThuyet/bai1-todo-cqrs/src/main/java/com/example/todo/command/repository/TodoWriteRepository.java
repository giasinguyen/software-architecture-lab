package com.example.todo.command.repository;

import com.example.todo.command.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoWriteRepository extends JpaRepository<TodoEntity, Long> {
}

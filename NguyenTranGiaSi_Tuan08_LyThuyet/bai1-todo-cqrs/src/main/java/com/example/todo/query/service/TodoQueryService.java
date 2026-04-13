package com.example.todo.query.service;

import com.example.todo.query.model.TodoView;
import com.example.todo.query.repository.TodoReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoQueryService {

    private final TodoReadRepository readRepository;

    public List<TodoView> getAll() {
        return readRepository.findAll();
    }

    public TodoView getById(Long id) {
        return readRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found: " + id));
    }
}

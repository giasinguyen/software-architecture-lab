package com.example.todo.command.service;

import com.example.todo.command.dto.CreateTodoRequest;
import com.example.todo.command.dto.UpdateTodoRequest;
import com.example.todo.command.model.TodoEntity;
import com.example.todo.command.repository.TodoWriteRepository;
import com.example.todo.query.model.TodoView;
import com.example.todo.query.repository.TodoReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoCommandService {

    private final TodoWriteRepository writeRepository;
    private final TodoReadRepository readRepository;

    @Transactional
    public TodoEntity create(CreateTodoRequest request) {
        TodoEntity entity = TodoEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .build();
        entity = writeRepository.save(entity);

        // Sync to read model
        readRepository.save(new TodoView(entity.getId(), entity.getTitle(), entity.getDescription(), entity.isCompleted()));
        return entity;
    }

    @Transactional
    public TodoEntity update(Long id, UpdateTodoRequest request) {
        TodoEntity entity = writeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found: " + id));
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCompleted(request.isCompleted());
        entity = writeRepository.save(entity);

        // Sync to read model
        readRepository.save(new TodoView(entity.getId(), entity.getTitle(), entity.getDescription(), entity.isCompleted()));
        return entity;
    }

    @Transactional
    public void delete(Long id) {
        writeRepository.deleteById(id);
        readRepository.deleteById(id);
    }
}

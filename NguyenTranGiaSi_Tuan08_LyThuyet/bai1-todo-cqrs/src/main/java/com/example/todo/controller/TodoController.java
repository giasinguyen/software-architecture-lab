package com.example.todo.controller;

import com.example.todo.command.dto.CreateTodoRequest;
import com.example.todo.command.dto.UpdateTodoRequest;
import com.example.todo.command.model.TodoEntity;
import com.example.todo.command.service.TodoCommandService;
import com.example.todo.query.model.TodoView;
import com.example.todo.query.service.TodoQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoCommandService commandService;
    private final TodoQueryService queryService;

    // ===== COMMAND =====

    @PostMapping
    public ResponseEntity<TodoEntity> create(@RequestBody CreateTodoRequest request) {
        return ResponseEntity.ok(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoEntity> update(@PathVariable Long id, @RequestBody UpdateTodoRequest request) {
        return ResponseEntity.ok(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== QUERY =====

    @GetMapping
    public ResponseEntity<List<TodoView>> getAll() {
        return ResponseEntity.ok(queryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoView> getById(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(id));
    }
}

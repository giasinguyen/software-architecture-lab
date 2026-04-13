package com.example.todo.command.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateTodoRequest {
    private String title;
    private String description;
    private boolean completed;
}

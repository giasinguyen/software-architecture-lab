package com.example.todo.command.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateTodoRequest {
    private String title;
    private String description;
}

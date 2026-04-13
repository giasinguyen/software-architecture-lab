package com.example.todo.query.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo_view")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TodoView {
    @Id
    private Long id;
    private String title;
    private String description;
    private boolean completed;
}

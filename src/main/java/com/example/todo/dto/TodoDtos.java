package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TodoDtos {

    private TodoDtos() {
    }

    public record TodoRequest(
            @NotBlank @Size(max = 255) String title,
            Boolean completed,
            @NotNull Long userId
    ) {
    }

    public record TodoResponse(
            Long id,
            String title,
            boolean completed,
            Long userId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}

package com.example.todo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UserDtos {

    private UserDtos() {
    }

    public record UserRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 255) String password
    ) {
    }

    public record UserResponse(
            Long id,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}

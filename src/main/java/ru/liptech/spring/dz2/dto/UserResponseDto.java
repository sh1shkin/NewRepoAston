package ru.liptech.spring.dz2.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
        Integer id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {}
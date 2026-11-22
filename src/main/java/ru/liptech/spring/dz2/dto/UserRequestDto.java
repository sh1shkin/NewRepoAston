package ru.liptech.spring.dz2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "Имя не может быть пустым")
        @Size(max = 120, message = "Имя не должно превышать 120 символов")
        String name,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @Size(max = 120, message = "Email не должен превышать 120 символов")
        String email,

        @Positive(message = "Возраст должен быть положительным числом")
        Integer age
) {}
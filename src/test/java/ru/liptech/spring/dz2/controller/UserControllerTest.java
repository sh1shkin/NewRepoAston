package ru.liptech.spring.dz2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.liptech.spring.dz2.dto.UserRequestDto;
import ru.liptech.spring.dz2.dto.UserResponseDto;
import ru.liptech.spring.dz2.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UserResponseDto sampleResponse = new UserResponseDto(
            1, "Иван", "ivan@example.com", 25, LocalDateTime.now()
    );

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        when(userService.findById(1)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.findById(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "не найден"));

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldCreateUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Иван", "ivan@example.com", 25);
        when(userService.create(any(UserRequestDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"));

        verify(userService, times(1)).create(any(UserRequestDto.class));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        UserRequestDto invalid = new UserRequestDto("", "ivan@example.com", 25);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        UserRequestDto invalid = new UserRequestDto("Иван", "not-an-email", 25);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        UserRequestDto request = new UserRequestDto("Иван", "ivan@example.com", 25);
        when(userService.create(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email занят"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldUpdateUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Пётр", "petr@example.com", 30);
        UserResponseDto updated = new UserResponseDto(
                1, "Пётр", "petr@example.com", 30, LocalDateTime.now()
        );
        when(userService.update(eq(1), any(UserRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Пётр"))
                .andExpect(jsonPath("$.email").value("petr@example.com"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Пётр", "petr@example.com", 30);
        when(userService.update(eq(999), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "не найден"));

        mockMvc.perform(put("/api/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).delete(1);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingUser() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "не найден"))
                .when(userService).delete(999);

        mockMvc.perform(delete("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }
}
package ru.liptech.spring.dz2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.liptech.spring.dz2.dto.UserRequestDto;
import ru.liptech.spring.dz2.dto.UserResponseDto;
import ru.liptech.spring.dz2.entity.User;
import ru.liptech.spring.dz2.repository.UserRepository;
import ru.liptech.spring.dz2.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_validData_callsRepositoryAndReturnsDto() {
        // Подготовка
        UserRequestDto request = new UserRequestDto("Max", "max@mail.ru", 22);
        User savedUser = new User().setName("Max").setEmail("max@mail.ru").setAge(22);

        when(userRepository.existsByEmail("max@mail.ru")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Выполнение
        UserResponseDto result = userService.create(request);

        // Проверка
        assertEquals("Max", result.name());
        assertEquals("max@mail.ru", result.email());
        assertEquals(22, result.age());
        verify(userRepository, times(1)).existsByEmail("max@mail.ru");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void create_emailAlreadyExists_throwsConflict() {
        UserRequestDto request = new UserRequestDto("Max", "max@mail.ru", 22);
        when(userRepository.existsByEmail("max@mail.ru")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.create(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_returnsListFromRepository() {
        User user = new User().setName("A").setEmail("a@mail.ru");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).name());
        verify(userRepository).findAll();
    }

    @Test
    void findById_userNotFound_throwsNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.findById(99));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void update_userNotFound_throwsNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        UserRequestDto request = new UserRequestDto("New", "new@mail.ru", 30);

        assertThrows(ResponseStatusException.class,
                () -> userService.update(99, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_userNotFound_throwsNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> userService.delete(99));

        verify(userRepository, never()).deleteById(anyInt());
    }
}
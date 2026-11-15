package ru.liptech.spring.dz2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.liptech.spring.dz2.dao.UserDao;
import ru.liptech.spring.dz2.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void create_validData_callsDaoAndReturnsUser() {
        when(userDao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.create("Max", "max@mail.ru", 22);

        assertEquals("Max", result.getName());
        assertEquals("max@mail.ru", result.getEmail());
        assertEquals(22, result.getAge());
        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    void create_invalidAge_throwsAndDoesNotCallDao() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.create("Max", "max@mail.ru", -5));
        verify(userDao, never()).create(any());
    }

    @Test
    void getAll_returnsListFromDao() {
        when(userDao.readUsers()).thenReturn(
                List.of(new User().setName("A").setEmail("a@mail.ru"))
        );

        List<User> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
        verify(userDao).readUsers();
    }

    @Test
    void getAll_whenDaoReturnsNull_returnsEmptyList() {
        when(userDao.readUsers()).thenReturn(null);

        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    void update_userNotFound_throws() {
        when(userDao.readUsers()).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> userService.update(99, "New", null, null));
        verify(userDao, never()).update(any());
    }

    @Test
    void delete_userNotFound_returnsFalse() {
        when(userDao.readUsers()).thenReturn(List.of());

        assertFalse(userService.delete(99));
        verify(userDao, never()).delete(any());
    }
}
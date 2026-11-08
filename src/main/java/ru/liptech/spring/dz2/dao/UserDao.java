package ru.liptech.spring.dz2.dao;

import ru.liptech.spring.dz2.model.User;

import java.util.List;

public interface UserDao {
    User create(User user);
    User update(User user);
    List<User> readUsers();
    boolean delete(User user);
}

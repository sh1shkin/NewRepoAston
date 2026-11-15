package ru.liptech.spring.dz2.service;

import ru.liptech.spring.dz2.dao.UserDao;
import ru.liptech.spring.dz2.dao.impl.UserDaoImpl;
import ru.liptech.spring.dz2.entity.User;
import ru.liptech.spring.dz2.util.UserBuilder;

import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User create(String name, String email, Integer age) {
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("возраст должен быть в диапазоне 0–150");
        }
        User user = new UserBuilder()
                .setName(name)
                .setEmail(email)
                .setAge(age)
                .build();
        return userDao.create(user);
    }

    public List<User> getAll() {
        List<User> users = userDao.readUsers();
        return users != null ? users : List.of();
    }

    public User findById(Integer id) {
        List<User> users = userDao.readUsers();
        if (users == null) {
            return null;
        }
        for (User u : users) {
            if (u.getId() != null && u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public User update(Integer id, String name, String email, Integer age) {
        User existing = findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        if (name != null && !name.isBlank()) {
            existing.setName(name.trim());
        }
        if (email != null && !email.isBlank()) {
            existing.setEmail(email.trim());
        }
        if (age != null) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("возраст должен быть в диапазоне 0–150");
            }
            existing.setAge(age);
        }
        return userDao.update(existing);
    }

    public boolean delete(Integer id) {
        User existing = findById(id);
        if (existing == null) {
            return false;
        }
        return userDao.delete(existing);
    }
}
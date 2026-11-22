package ru.liptech.spring.dz2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.liptech.spring.dz2.dto.UserRequestDto;
import ru.liptech.spring.dz2.dto.UserResponseDto;
import ru.liptech.spring.dz2.entity.User;
import ru.liptech.spring.dz2.repository.UserRepository;
import ru.liptech.spring.dz2.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Пользователь с id=" + id + " не найден"));
        return toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email уже занят: " + request.email());
        }
        User user = new User()
                .setName(request.name())
                .setEmail(request.email())
                .setAge(request.age());

        User saved = userRepository.save(user);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto update(Integer id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Пользователь с id=" + id + " не найден"));
        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email уже занят: " + request.email());
        }

        user.setName(request.name())
                .setEmail(request.email())
                .setAge(request.age());

        User updated = userRepository.save(user);
        return toResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
    }
    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
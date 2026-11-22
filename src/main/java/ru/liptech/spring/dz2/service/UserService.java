package ru.liptech.spring.dz2.service;

import ru.liptech.spring.dz2.dto.UserRequestDto;
import ru.liptech.spring.dz2.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();
    UserResponseDto findById(Integer id);
    UserResponseDto create(UserRequestDto request);
    UserResponseDto update(Integer id, UserRequestDto request);
    void delete(Integer id);
}
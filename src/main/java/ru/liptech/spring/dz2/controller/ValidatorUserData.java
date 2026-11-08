package ru.liptech.spring.dz2.controller;

public final class ValidatorUserData {
    public static void validate(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name обязателен");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email обязателен");
        }
    }
}

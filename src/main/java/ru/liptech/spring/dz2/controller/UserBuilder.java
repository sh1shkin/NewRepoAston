package ru.liptech.spring.dz2.controller;

import ru.liptech.spring.dz2.model.User;

public class UserBuilder {
    private String name;
    private String email;
    private Integer age;

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setAge(Integer age) {
        this.age = age;
        return this;
    }

    public User build() {
        ValidatorUserData.validate(this.name, this.email);
        return new User().setName(this.name).setEmail(this.email).setAge(this.age);
    }
}

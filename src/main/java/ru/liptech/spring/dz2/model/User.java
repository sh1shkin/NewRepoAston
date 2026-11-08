package ru.liptech.spring.dz2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private String created_at;

}

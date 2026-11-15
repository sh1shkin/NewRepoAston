package ru.liptech.spring.dz2.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.liptech.spring.dz2.dao.impl.UserDaoImpl;
import ru.liptech.spring.dz2.entity.User;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplIT {

    static {
        System.setProperty("docker.host", "unix:///var/run/docker.sock");
        System.setProperty("api.version", "1.44");
    }
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("userdb")
            .withUsername("test")
            .withPassword("test");

    static SessionFactory sessionFactory;
    UserDao userDao;

    @BeforeAll
    static void initHibernate() {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        cfg.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", postgres.getUsername());
        cfg.setProperty("hibernate.connection.password", postgres.getPassword());
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    @AfterAll
    static void closeHibernate() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
    }

    @Test
    void create_assignsIdAndPersists() {
        String email = "test-" + UUID.randomUUID() + "@mail.ru";
        User user = new User().setName("Test").setEmail(email).setAge(20);

        User saved = userDao.create(user);

        assertNotNull(saved.getId());
        List<User> all = userDao.readUsers();
        assertTrue(all.stream().anyMatch(u -> email.equals(u.getEmail())));
    }

    @Test
    void update_changesFields() {
        String email = "old-" + UUID.randomUUID() + "@mail.ru";
        User user = userDao.create(new User().setName("Old").setEmail(email));
        user.setName("New");

        User updated = userDao.update(user);

        assertEquals("New", updated.getName());
    }

    @Test
    void delete_removesUser() {
        String email = "del-" + UUID.randomUUID() + "@mail.ru";
        User user = userDao.create(new User().setName("Del").setEmail(email));

        assertTrue(userDao.delete(user));

        List<User> all = userDao.readUsers();
        assertTrue(all == null || all.stream().noneMatch(u -> email.equals(u.getEmail())));
    }

    @Test
    void delete_alreadyDeleted_returnsFalse() {
        String email = "tmp-" + UUID.randomUUID() + "@mail.ru";
        User user = userDao.create(new User().setName("Tmp").setEmail(email));
        userDao.delete(user);

        assertFalse(userDao.delete(user));
    }
}
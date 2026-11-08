package ru.liptech.spring.dz2;

import ru.liptech.spring.dz2.controller.UserBuilder;
import ru.liptech.spring.dz2.controller.UserDaoController;
import ru.liptech.spring.dz2.dao.UserDao;
import ru.liptech.spring.dz2.db.DataBaseConnector;
import ru.liptech.spring.dz2.model.User;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final UserDao userDao = new UserDaoController();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        try {
            app.run();
        } finally {
            DataBaseConnector.closeSession();
        }
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> showAllUsers();
                    case "3" -> updateUser();
                    case "4" -> deleteUser();
                    case "0" -> {
                        System.out.println("Выход.");
                        running = false;
                    }
                    default -> System.out.println("Неверный пункт меню.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("--- Меню ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Обновить пользователя");
        System.out.println("4. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Возраст (можно пусто): ");
        String ageStr = scanner.nextLine().trim();

        Integer age = null;
        if (!ageStr.isEmpty()) {
            age = Integer.parseInt(ageStr);
        }

        User user = new UserBuilder()
                .setName(name)
                .setEmail(email)
                .setAge(age)
                .build();

        User saved = userDao.create(user);
        System.out.println("Создан: id=" + saved.getId()
                + ", name=" + saved.getName()
                + ", email=" + saved.getEmail());
    }

    private void showAllUsers() {
        List<User> users = userDao.readUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("Пользователей нет.");
            return;
        }
        for (User u : users) {
            System.out.println("id=" + u.getId()
                    + " | name=" + u.getName()
                    + " | email=" + u.getEmail()
                    + " | age=" + u.getAge()
                    + " | createdAt=" + u.getCreatedAt());
        }
    }

    private void updateUser() {
        System.out.print("ID пользователя: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        List<User> users = userDao.readUsers();
        User existing = null;
        if (users != null) {
            for (User u : users) {
                if (u.getId() != null && u.getId() == id) {
                    existing = u;
                    break;
                }
            }
        }
        if (existing == null) {
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Новое имя [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            existing.setName(name);
        }

        System.out.print("Новый email [" + existing.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            existing.setEmail(email);
        }

        System.out.print("Новый возраст [" + existing.getAge() + "]: ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) {
            existing.setAge(Integer.parseInt(ageStr));
        }

        User updated = userDao.update(existing);
        System.out.println("Обновлён: " + updated.getName() + " / " + updated.getEmail());
    }

    private void deleteUser() {
        System.out.print("ID для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        User user = new User();

        List<User> users = userDao.readUsers();
        User toDelete = null;
        if (users != null) {
            for (User u : users) {
                if (u.getId() != null && u.getId() == id) {
                    toDelete = u;
                    break;
                }
            }
        }
        if (toDelete == null) {
            System.out.println("Пользователь не найден.");
            return;
        }

        boolean deleted = userDao.delete(toDelete);
        System.out.println(deleted ? "Удалён." : "Не удалось удалить.");
    }
}
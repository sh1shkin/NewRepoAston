package ru.liptech.spring.dz2;

import ru.liptech.spring.dz2.config.HibernateConfig;
import ru.liptech.spring.dz2.entity.User;
import ru.liptech.spring.dz2.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {

    private final UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        try {
            app.run();
        } finally {
            HibernateConfig.closeSession();
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
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
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

        Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);

        User saved = userService.create(name, email, age);
        System.out.println("Создан: id=" + saved.getId()
                + ", name=" + saved.getName()
                + ", email=" + saved.getEmail());
    }

    private void showAllUsers() {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
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

        User existing = userService.findById(id);
        if (existing == null) {
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Новое имя [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = null;
        }

        System.out.print("Новый email [" + existing.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            email = null;
        }

        System.out.print("Новый возраст [" + existing.getAge() + "]: ");
        String ageStr = scanner.nextLine().trim();
        Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);

        User updated = userService.update(id, name, email, age);
        System.out.println("Обновлён: " + updated.getName() + " / " + updated.getEmail());
    }

    private void deleteUser() {
        System.out.print("ID для удаления: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        boolean deleted = userService.delete(id);
        System.out.println(deleted ? "Удалён." : "Пользователь не найден.");
    }
}
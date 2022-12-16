package data;

import com.github.javafaker.Faker;
import models.user.User;

import java.util.List;
import java.util.Locale;
import java.util.Random;

// Класс для генерации пользователя
public class UserGenerator {

    private static Faker fakerEn = new Faker();
    private static Faker fakerRu = new Faker(Locale.forLanguageTag("ru"));

    // Метод возвращает уникальный email
    public static String getUniqueEmail() {
        Random random = new Random();
        String email = fakerRu.internet().emailAddress(fakerEn.name().username() + random.nextInt(1000));
        return email;
    }

    // Метод возвращает курьера с уникальными данными
    public static User getUniqueUser() {
        String name = fakerRu.name().name();
        String password = fakerEn.internet().password();
        return new User(getUniqueEmail(), password, name);
    }

    // Метод возвращает пользователя c пустым email
    public static User getUserEmptyEmail() {
        User user = UserGenerator.getUniqueUser();
        user.setEmail("");
        return user;
    }

    // Метод возвращает пользователя c null email
    public static User getUserNullEmail() {
        User user = UserGenerator.getUniqueUser();
        user.setEmail(null);
        return user;
    }

    // Метод возвращает пользователя c пустым паролем
    public static User getUserEmptyPassword() {
        User user = UserGenerator.getUniqueUser();
        user.setPassword("");
        return user;
    }

    // Метод возвращает пользователя c null паролем
    public static User getUserNullPassword() {
        User user = UserGenerator.getUniqueUser();
        user.setPassword(null);
        return user;
    }

    // Метод возвращает пользователя c пустым именем
    public static User getUserEmptyName() {
        User user = UserGenerator.getUniqueUser();
        user.setName("");
        return user;
    }

    // Метод возвращает пользователя c null именем
    public static User getUserNullName() {
        User user = UserGenerator.getUniqueUser();
        user.setName(null);
        return user;
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го пустым емейлом.
    public static List<User> getUserListEmptyLogin() {
        User user = UserGenerator.getUniqueUser();
        User userLogin = new User("", user.getPassword(), user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го null емейлом.
    public static List<User> getUserListNullLogin() {
        User user = UserGenerator.getUniqueUser();
        User userLogin = new User(null, user.getPassword(), user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го пустым паролем.
    public static List<User> getUserListEmptyPassword() {
        User user = UserGenerator.getUniqueUser();
        User userLogin = new User(user.getEmail(), "", user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го null паролем.
    public static List<User> getUserListNullPassword() {
        User user = UserGenerator.getUniqueUser();
        User userLogin = new User(user.getEmail(), null, user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го неверным емейлом.
    public static List<User> getUserListErrorInLogin() {
        User user = UserGenerator.getUniqueUser();
        String email = user.getEmail();
        String newEmail = email.substring(1);
        User userLogin = new User(newEmail, user.getPassword(), user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го неверным паролем.
    public static List<User> getUserListErrorInPassword() {
        User user = UserGenerator.getUniqueUser();
        String password = user.getPassword();
        String newPassword = password.substring(1);
        User userLogin = new User(user.getEmail(), newPassword, user.getName());
        return List.of(user, userLogin);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го новым емейлом.
    public static List<User> getUserChangeEmail() {
        User user = UserGenerator.getUniqueUser();
        String newEmail = getUniqueEmail();
        User changedUser = new User(newEmail, user.getPassword(), user.getName());
        return List.of(user, changedUser);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го новым паролем.
    public static List<User> getUserChangePassword() {
        User user = UserGenerator.getUniqueUser();
        String newPassword = fakerRu.internet().password();
        User changedUser = new User(user.getEmail(), newPassword, user.getName());
        return List.of(user, changedUser);
    }

    // Метод возвращает список из 2х пользователей: 2й отличается от 1го новым именем.
    public static List<User> getUserChangeName() {
        User user = UserGenerator.getUniqueUser();
        String newName = fakerRu.name().name();
        User changedUser = new User(user.getEmail(), user.getPassword(), newName);
        return List.of(user, changedUser);
    }
}

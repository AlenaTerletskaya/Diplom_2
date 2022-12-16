package models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Класс POJO данных для авторизации пользователя
@AllArgsConstructor
@Getter
@Setter
public class Credentials {

    private String email;
    private String password;

    // Статический метод, который создает экземпляр Credentials на основе пользователя.
    public static Credentials from(User user) {
        return new Credentials(user.getEmail(), user.getPassword());
    }
}

package models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Класс POJO для пользователя
@AllArgsConstructor
@Getter
@Setter
public class User {

    private String email;
    private String password;
    private String name;
}

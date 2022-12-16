package models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// Класс POJO для пользователя
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    private String email;
    private String password;
    private String name;
}

package models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Класс POJO для выхода пользователя из системы
@AllArgsConstructor
@Getter
@Setter
public class UserLogout {

    private String token;

}

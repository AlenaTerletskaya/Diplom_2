package user.login;

import clients.UserClient;
import data.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.ValidatableResponse;
import models.user.Credentials;
import models.user.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

// Класс с параметризованным тестом по авторизации пользователя с неправильными/ отсутствующими полями
@RunWith(Parameterized.class)
public class UserLoginInvalidEmailOrPasswordTest {

    private UserClient userClient;
    private List<User> users;
    private int statusCode;
    private String message;
    private String accessToken;
    private static final String MESSAGE = "email or password are incorrect";

    public UserLoginInvalidEmailOrPasswordTest(List<User> users, int statusCode, String message) {
        this.users = users;
        this.statusCode = statusCode;
        this.message = message;
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }

    // Тестовые данные
    @Parameterized.Parameters (name = "Тест. данные - пользователи: {0}, статус-код: {1}, сообщение: {2}")
    public static Object[][] getTestData() {
        return new Object [][] {
                {UserGenerator.getUserListEmptyLogin(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserListNullLogin(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserListEmptyPassword(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserListNullPassword(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserListErrorInLogin(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserListErrorInPassword(), SC_UNAUTHORIZED, MESSAGE}
        };
    }

    @Test
    @Tag("Negative")
    @DisplayName("Authorization without required field or with invalid field returns an error")
    @Description("Проверяет, что для авторизации нужно передать все обязательные поля. " +
            "Если одного из полей нет или данные в поле не верные, " +
            "запрос возвращает ошибку 401 и соответствующий текст, поле \"success\" = false.")
    public void userLoginInvalidFieldOrNoField_returnError() {
        // Создаем пользователя
        ValidatableResponse responseCreate = userClient.createUser(users.get(0));
        accessToken = responseCreate.extract().path("accessToken");
        // Авторизуемся под пользователем без обязательного поля / с неверным полем.
        ValidatableResponse responseWrongLogin = userClient.loginUser(Credentials.from(users.get(1)));
        int actualStatusCode = responseWrongLogin.extract().statusCode(); // Получаем статус-код ответа
        String actualMessage = responseWrongLogin.extract().path("message"); // Получаем текст сообщения
        // Проверяем статус-код, пользователь не авторизован.
        Assert.assertEquals("Status code should be equal to " + statusCode, statusCode, actualStatusCode);
        // Проверяем текст сообщения.
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }
}

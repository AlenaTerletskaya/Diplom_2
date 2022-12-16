package user.change_user;

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

import static org.apache.http.HttpStatus.SC_OK;

// Класс с параметризованным тестом по изменению данных авторизованного пользователя
@RunWith(Parameterized.class)
public class AuthorizedUserChangeTest {

    private UserClient userClient;
    private List<User> users;
    private int statusCode;
    private String accessToken;

    public AuthorizedUserChangeTest(List<User> users, int statusCode) {
        this.users = users;
        this.statusCode = statusCode;
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
    @Parameterized.Parameters (name = "Тест. данные - пользователи: {0}, статус-код: {1} ")
    public static Object[][] getTestData() {
        return new Object [][] {
                {UserGenerator.getUserChangeEmail(), SC_OK},
                {UserGenerator.getUserChangePassword(), SC_OK},
                {UserGenerator.getUserChangeName(), SC_OK}
        };
    }

    @Test
    @Tag("Positive")
    @DisplayName("The authorized user can be changed")
    @Description("Проверяет, что можно изменить данные авторизованного пользователя. " +
            "Успешный запрос возвращает код 200 и json. Поле \"success\" = true. " +
            "Значение измененного поля соответствует переданному в запросе." +
            "Под новыми данными можно авторизоваться.")
    public void authorizedUserCanBeChanged() {
        // Создаем пользователя
        userClient.createUser(users.get(0));
        // Авторизуемся под пользователем.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(users.get(0)));
        accessToken = responseLogin.extract().path("accessToken");
        // Меняем данные пользователя
        ValidatableResponse responseChange = userClient.changeUser(users.get(1), accessToken);
        int actualStatusCode = responseChange.extract().statusCode(); // Получаем статус-код ответа
        boolean isUserChanged = responseChange.extract().path("success"); // Получаем значение поля "success"
        String email = responseChange.extract().path("user.email"); // Получаем емейл из ответа
        String newEmail = users.get(1).getEmail(); // Получаем емейл из новых данных
        String name = responseChange.extract().path("user.name"); // Получаем имя из ответа
        String newName = users.get(1).getName(); // Получаем имя из новых данных
        // Авторизуемся под новыми данными
        ValidatableResponse responseNewLogin = userClient.loginUser(Credentials.from(users.get(1)));
        // Проверяем, что авторизация прошла успешно.
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        // Проверяем, что изменение данных пользователя прошло успешно.
        Assert.assertEquals("Change status code should be equal to " + statusCode, statusCode, actualStatusCode);
        // Проверяем, что success = true.
        Assert.assertTrue("Success value should be equal to true.", isUserChanged);
        // Проверяем, что емейл соответствует новым данным.
        Assert.assertEquals("Email should be equal to " + newEmail, newEmail, email);
        // Проверяем, что имя соответствует новым данным.
        Assert.assertEquals("Name should be equal to \"" + newName + "\"", newName, name);
        // Проверяем, что под новыми данными можно авторизоваться.
        Assert.assertEquals("Login with new data status code should be equal to " + SC_OK,
                SC_OK, responseNewLogin.extract().statusCode());
    }
}

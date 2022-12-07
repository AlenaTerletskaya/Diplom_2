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

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

// Класс с параметризованным тестом по изменению данных неавторизованного пользователя
@RunWith(Parameterized.class)
public class UnauthorizedUserChangeTest {

    private UserClient userClient;
    private List<User> users;
    private int statusCode;
    private String message;
    private String accessToken;
    private static final String MESSAGE = "You should be authorised";

    public UnauthorizedUserChangeTest(List<User> users, int statusCode, String message) {
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
    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object [][] {
                {UserGenerator.getUserChangeEmail(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserChangePassword(), SC_UNAUTHORIZED, MESSAGE},
                {UserGenerator.getUserChangeName(), SC_UNAUTHORIZED, MESSAGE}
        };
    }

    @Test
    @Tag("Negative")
    @DisplayName("An unauthorized user cannot be changed")
    @Description("Проверяет, что нельзя изменить данные неавторизованного пользователя. " +
            "Если пользователь не авторизован, запрос возвращает ошибку 401 и соответствующий текст, " +
            "поле \"success\" = false. Авторизоваться под новыми данными нельзя.")
    public void unauthorizedUserCanNotBeChanged() {

        // Создаем пользователя
        ValidatableResponse responseCreate = userClient.createUser(users.get(0));
        accessToken = responseCreate.extract().path("accessToken");

        // Меняем данные пользователя
        ValidatableResponse responseChange = userClient.changeUser(users.get(1), accessToken);

        // Пытаемся авторизоваться под новыми данными.
        ValidatableResponse responseNewLogin = userClient.loginUser(Credentials.from(users.get(1)));

        // Проверяем, что изменение данных пользователя не произошло.
        int actualStatusCode = responseChange.extract().statusCode();
        Assert.assertEquals("An unauthorized user should not be changed. " +
                "Change status code should be equal to " + statusCode, statusCode, actualStatusCode);

        // Проверяем, что success = false.
        boolean isUserChanged = responseChange.extract().path("success");
        Assert.assertFalse("Success value should be equal to false.", isUserChanged);

        // Проверяем текст сообщения.
        String actualMessage = responseChange.extract().path("message");
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);

        // Проверяем, что под новыми данными нельзя авторизоваться.
        Assert.assertEquals("Login with new data status code should be equal to " + SC_UNAUTHORIZED,
                SC_UNAUTHORIZED, responseNewLogin.extract().statusCode());
    }
}

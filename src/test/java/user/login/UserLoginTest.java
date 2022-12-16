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

import static org.apache.http.HttpStatus.SC_OK;

// Класс с тестами по авторизации пользователя
public class UserLoginTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getUniqueUser();
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @Tag("Positive")
    @DisplayName("A registered user can log in")
    @Description("Проверяет, что зарегистрированный пользователь может авторизоваться; " +
            "успешный запрос возвращает код 200 и json. Поле \"success\" = true. " +
            "Значения полей user.email и user.name соответствуют переданным в запросе. " +
            "Поля accessToken и refreshToken не null.")
    public void registeredUserCanLogIn() {
        // Создаем пользователя и авторизуемся под ним.
        userClient.createUser(user);
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        int statusCode = responseLogin.extract().statusCode(); // Получаем статус-код ответа
        boolean isUserLogedIn = responseLogin.extract().path("success"); // Получаем значение поля "success"
        accessToken = responseLogin.extract().path("accessToken"); // Получаем accessToken
        String refreshToken = responseLogin.extract().path("refreshToken"); // Получаем refreshToken
        String email = responseLogin.extract().path("user.email"); // Получаем емейл
        String name = responseLogin.extract().path("user.name"); // Получаем имя
        // Проверяем, что авторизация прошла успешно, статус-код = 200.
        Assert.assertEquals("Login status code should be equal to " + SC_OK, SC_OK, statusCode);
        // Проверяем, что success = true.
        Assert.assertTrue("Success value should be equal to true.", isUserLogedIn);
        // Проверяем, что accessToken не null.
        Assert.assertNotNull("Access token should not be null." , accessToken);
        // Проверяем, что refreshToken не null.
        Assert.assertNotNull("Refresh token should not be null." , refreshToken);
        // Проверяем, что емейл авторизованного пользователя соответствует переданному в запросе.
        Assert.assertEquals("Email should be equal to " + user.getEmail(), user.getEmail(), email);
        // Проверяем, что имя авторизованного пользователя соответствует переданному в запросе.
        Assert.assertEquals("Name should be equal to \"" + user.getName() + "\"", user.getName(), name);
    }
}

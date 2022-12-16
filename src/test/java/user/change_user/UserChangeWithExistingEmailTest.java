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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

// Класс с тестом по изменению емейла пользователя на уже зарегистрированный емейл
public class UserChangeWithExistingEmailTest {

    private UserClient userClient;
    private String accessToken1;
    private String accessToken2;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken1);
        userClient.deleteUser(accessToken2);
    }

    @Test
    @Tag("Negative")
    @DisplayName("A user's email cannot be changed to an already registered email")
    @Description("Проверяет, что нельзя изменить емейл пользователя на уже зарегистрированный емейл. " +
            "Запрос возвращает ошибку 403 и соответствующий текст, поле \"success\" = false.")
    public void userEmailCanNotBeChangedToExistingEmail() {
        // Создаем двух пользователей
        User user1 = UserGenerator.getUniqueUser();
        userClient.createUser(user1);
        User user2 = UserGenerator.getUniqueUser();
        accessToken2 = userClient.createUser(user2).extract().path("accessToken");
        // Авторизуемся под пользователем1.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user1));
        accessToken1 = responseLogin.extract().path("accessToken");
        // Меняем email пользователя1 на email пользователя2.
        ValidatableResponse responseChange = userClient.changeUser(
                new User(user2.getEmail(), user1.getPassword(), user1.getName()), accessToken1);
        int actualStatusCode = responseChange.extract().statusCode(); // Получаем статус-код ответа
        boolean isUserChanged = responseChange.extract().path("success"); // Получаем значение поля "success"
        String actualMessage = responseChange.extract().path("message"); // Получаем текст сообщения
        String message = "User with such email already exists"; // Ожидаемое сообщение
        // Проверяем, что авторизация пользователя1 прошла успешно.
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        // Проверяем, что изменение данных пользователя не произошло.
        Assert.assertEquals("Change status code should be equal to " + SC_FORBIDDEN,
                SC_FORBIDDEN, actualStatusCode);
        // Проверяем, что success = false.
        Assert.assertFalse("Success value should be equal to false.", isUserChanged);
        // Проверяем текст сообщения.
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }
}

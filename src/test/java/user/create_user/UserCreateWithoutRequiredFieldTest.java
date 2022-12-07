package user.create_user;

import clients.UserClient;
import data.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;

// Класс с параметризованным тестом по созданию пользователя без обязательного поля
@RunWith(Parameterized.class)
public class UserCreateWithoutRequiredFieldTest {

    private UserClient userClient;
    private User user;
    private int statusCode;
    private String message;
    private static final String MESSAGE = "Email, password and name are required fields";

    public UserCreateWithoutRequiredFieldTest(User user, int statusCode, String message) {
        this.user = user;
        this.statusCode = statusCode;
        this.message = message;
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    // Тестовые данные
    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object [][] {
                {UserGenerator.getUserEmptyEmail(), SC_FORBIDDEN, MESSAGE},
                {UserGenerator.getUserNullEmail(), SC_FORBIDDEN, MESSAGE},
                {UserGenerator.getUserEmptyPassword(), SC_FORBIDDEN, MESSAGE},
                {UserGenerator.getUserNullPassword(), SC_FORBIDDEN, MESSAGE},
                {UserGenerator.getUserEmptyName(), SC_FORBIDDEN, MESSAGE},
                {UserGenerator.getUserNullName(), SC_FORBIDDEN, MESSAGE}
        };
    }

    @Test
    @Tag("Negative")
    @DisplayName("Creating a user without a required field returns an error")
    @Description("Проверяет, что для создания пользователя нужно передать все обязательные поля. " +
            "Если одного из полей нет, запрос возвращает ошибку 403 и соответствующий текст, " +
            "поле \"success\" = false.")
    public void userCreateWithoutRequiredField_returnError() {

        // Создаем пользователя без обязательного поля
        ValidatableResponse responseCreate = userClient.createUser(user);

        // Проверяем статус-код, пользователь не создан.
        int actualStatusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("Status code should be equal to " + statusCode, statusCode, actualStatusCode);

        // Проверяем текст сообщения.
        String actualMessage = responseCreate.extract().path("message");
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }

}

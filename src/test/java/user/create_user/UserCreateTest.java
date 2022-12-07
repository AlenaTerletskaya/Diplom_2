package user.create_user;

import clients.UserClient;
import data.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

// Класс с тестами по созданию пользователя
public class UserCreateTest {

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
    @DisplayName("Unique user can be created")
    @Description("Проверяет, что можно создать уникального пользователя. " +
            "Успешный запрос возвращает код 200 и json. Поле \"success\" = true. " +
            "Значения полей user.email и user.name соответствуют переданным в запросе. " +
            "Поля accessToken и refreshToken не null.")
    public void uniqueUserCanBeCreated() {

        // Создаем пользователя
        ValidatableResponse responseCreate = userClient.createUser(user);

        // Проверяем, что создание пользователя прошло успешно, статус-код = 200.
        int statusCode = responseCreate.extract().statusCode();
        Assert.assertEquals("Status code should be equal to " + SC_OK, SC_OK, statusCode);

        // Проверяем, что success = true.
        boolean isUserCreated = responseCreate.extract().path("success");
        Assert.assertTrue("Value should be equal to true", isUserCreated);

        // Проверяем, что емейл пользователя соответствует переданному в запросе.
        String email = responseCreate.extract().path("user.email");
        Assert.assertEquals("Email should be equal to " + user.getEmail(), user.getEmail(), email);

        // Проверяем, что имя пользователя соответствует переданному в запросе.
        String name = responseCreate.extract().path("user.name");
        Assert.assertEquals("Name should be equal to " + user.getName(), user.getName(), name);

        // Проверяем, что accessToken не null.
        accessToken = responseCreate.extract().path("accessToken");
        Assert.assertNotNull("Access token should not be null" , accessToken);

        // Проверяем, что refreshToken не null.
        String refreshToken = responseCreate.extract().path("refreshToken");
        Assert.assertNotNull("Refresh token should not be null" , refreshToken);

    }

    @Test
    @Tag("Negative")
    @DisplayName("Creating two identical users returns an error")
    @Description("Проверяет, что нельзя создать пользователя, который уже зарегистрирован. " +
            "Eсли создать пользователя с email, который уже существует, " +
            "возвращается ошибка 403 и соответствующий текст, поле \"success\" = false.")
    public void twoEqualUsersCanNotBeCreated() {

        // Создаем первого пользователя
        ValidatableResponse responseFirst = userClient.createUser(user);
        accessToken = responseFirst.extract().path("accessToken");

        // Создаем второго пользователя, идентичного первому
        ValidatableResponse responseSecond = userClient.createUser(user);

        // Проверяем, что второй пользователь не создан, статус-код = 403.
        int secondStatusCode = responseSecond.extract().statusCode();
        Assert.assertEquals("Status code should be equal to " + SC_FORBIDDEN,
                SC_FORBIDDEN, secondStatusCode);

        // Проверяем, что success = false.
        Boolean isSuccessTrue = responseSecond.extract().path("success");
        Assert.assertFalse("Success value should be equal to false.", isSuccessTrue);

        // Проверяем текст сообщения.
        String secondMessage = responseSecond.extract().path("message");
        String expectedMessage = "User already exists";
        Assert.assertEquals("Message should be equal to \"" + expectedMessage + "\"", expectedMessage, secondMessage);
    }

}

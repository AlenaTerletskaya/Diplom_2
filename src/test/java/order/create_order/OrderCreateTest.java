package order.create_order;

import clients.OrderClient;
import clients.UserClient;
import data.OrderGenerator;
import data.UserGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.ValidatableResponse;
import models.user.Credentials;
import models.order.Order;
import models.user.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;


// Класс с тестами по созданию заказа
public class OrderCreateTest {

    private UserClient userClient;
    private User user;
    private String accessToken;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        // Создаем пользователя
        user = UserGenerator.getUniqueUser();
        ValidatableResponse responseCreate = userClient.createUser(user);
        accessToken = responseCreate.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessToken);
    }

    @Test
    @Tag("Positive")
    @DisplayName("An authorized user can create an order with ingredients")
    @Description("Проверяет, что авторизованный пользователь может создать заказ с ингредиентами. " +
            "Успешный запрос возвращает код 200 и json. Поле \"success\" = true. " +
            "Id заказа не null. Имя и емейл заказчика совпадают с данными пользователя.")
    public void authUserCanCreateOrderWithIngr() {
        // Авторизуемся под пользователем.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        // Создаем новый заказ, используя токен авторизованного пользователя.
        Order newOrder = OrderGenerator.getNewOrder(orderClient);
        ValidatableResponse responseOrder = orderClient.createOrderWithToken(newOrder, accessToken);
        int statusCode = responseOrder.extract().statusCode(); // Получаем статус-код ответа
        boolean isOrderCreated = responseOrder.extract().path("success"); // Получаем значение поля "success"
        String orderId = responseOrder.extract().path("order._id"); // Получаем значение id заказа
        String ownerName = responseOrder.extract().path("order.owner.name"); // Получаем имя заказчика
        String ownerEmail = responseOrder.extract().path("order.owner.email"); // Получаем емейл заказчика
        // Проверяем, что авторизация прошла успешно.
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        // Проверяем, что создание заказа прошло успешно.
        Assert.assertEquals("Status code should be equal to " + SC_OK, SC_OK, statusCode);
        // Проверяем, что success = true.
        Assert.assertTrue("Value should be equal to true", isOrderCreated);
        // Проверяем, чтo id заказа не null.
        Assert.assertNotNull("The order id should not be null" , orderId);
        // Проверяем, чтo имя заказчика совпадает с именем пользователя.
        Assert.assertEquals("Owner name should be equal to " + user.getName(), user.getName(), ownerName);
        // Проверяем, чтo емейл заказчика совпадает с емейлом пользователя.
        Assert.assertEquals("Owner email should be equal to " + user.getEmail(), user.getEmail(), ownerEmail);
    }

    @Test
    @Tag("Negative")
    @DisplayName("An unauthorized user can not create an order")
    @Description("Проверяет, что неавторизованный пользователь не может создать заказ. " +
            "Если пользователь не авторизован, запрос возвращает код ответа 401.")
    public void unauthUserCanNotCreateOrder() {
        // Создаем новый заказ, используя токен неавторизованного пользователя.
        Order newOrder = OrderGenerator.getNewOrder(orderClient);
        ValidatableResponse responseOrder = orderClient.createOrderWithToken(newOrder, accessToken);
        // Проверяем, что заказ не создан. Статус-код ответа = 401.
        int statusCode = responseOrder.extract().statusCode();
        Assert.assertEquals("An unauthorized user should not create an order. " +
                "Status code should be equal to " + SC_UNAUTHORIZED, SC_UNAUTHORIZED, statusCode);
    }

    @Test
    @Tag("Negative")
    @DisplayName("A request to create an order with invalid ingredient returns an error")
    @Description("Проверяет, что если в запросе по созданию заказа передан невалидный ингредиент, " +
            "вернётся код ответа 400 и соответствующий текст, поле \"success\" = false. ")
    public void createOrderWithInvalidIngredient() {
        // Авторизуемся под пользователем.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        // Создаем новый заказ с невалидным ингредиентом.
        Order OrderWithInvalidIngr = OrderGenerator.getOrderWithInvalidIngr(orderClient);
        ValidatableResponse responseOrder = orderClient.createOrderWithToken(OrderWithInvalidIngr, accessToken);
        int statusCode = responseOrder.extract().statusCode(); // Получаем статус-код ответа
        boolean isOrderCreated = responseOrder.extract().path("success"); // Получаем значение поля "success"
        String actualMessage = responseOrder.extract().path("message"); // Получаем текст сообщения
        String message = "One or more ids provided are incorrect"; // Ожидаемое сообщение
        // Проверяем, что авторизация прошла успешно.
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        // Проверяем, что заказ не создан. Статус-код = 400.
        Assert.assertEquals("You can not create an order with invalid ingredient. " +
                "Status code should be equal to " + SC_BAD_REQUEST, SC_BAD_REQUEST, statusCode);
        // Проверяем, что success = false.
        Assert.assertFalse("Success value should be equal to false.", isOrderCreated);
        // Проверяем текст сообщения.
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }

    @Test
    @Tag("Negative")
    @DisplayName("A request to create an order without ingredients returns an error")
    @Description("Проверяет, что запрос по созданию заказа без ингредиентов " +
            "возвращает код ответа 400 и соответствующий текст, поле \"success\" = false. ")
    public void createOrderWithoutIngredients() {
        // Авторизуемся под пользователем.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        accessToken = responseLogin.extract().path("accessToken");
        // Создаем новый заказ без ингредиентов.
        Order OrderWithoutIngrs = OrderGenerator.getOrderWithoutIngredients();
        ValidatableResponse responseOrder = orderClient.createOrderWithToken(OrderWithoutIngrs, accessToken);
        int statusCode = responseOrder.extract().statusCode(); // Получаем статус-код ответа
        boolean isOrderCreated = responseOrder.extract().path("success"); // Получаем значение поля "success"
        String actualMessage = responseOrder.extract().path("message"); // Получаем текст сообщения
        String message = "Ingredient ids must be provided"; // Ожидаемое сообщение
        // Проверяем, что авторизация прошла успешно.
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        // Проверяем, что заказ не создан. Статус-код = 400.
        Assert.assertEquals("You can not create an order with invalid ingredient. " +
                "Status code should be equal to " + SC_BAD_REQUEST, SC_BAD_REQUEST, statusCode);
        // Проверяем, что success = false.
        Assert.assertFalse("Success value should be equal to false.", isOrderCreated);
        // Проверяем текст сообщения.
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }
}

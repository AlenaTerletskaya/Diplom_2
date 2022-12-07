package order.get_orders;

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
import models.user.UserLogout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

// Класс с тестами по получению заказов пользователя
public class GetOrdersTest {
    private UserClient userClient;
    private User user;
    private String accessTokenCreate;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        // Создаем пользователя
        user = UserGenerator.getUniqueUser();
        ValidatableResponse responseCreate = userClient.createUser(user);
        accessTokenCreate = responseCreate.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        userClient.deleteUser(accessTokenCreate);
    }

    @Test
    @Tag("Positive")
    @DisplayName("An authorized user can get a list of his orders")
    @Description("Проверяет, что авторизованный пользователь может получить список своих заказов. " +
            "Успешный запрос возвращает код 200 и json. Поле \"success\" = true.")
    public void authUserCanGetHisOrders() {

        // Авторизуемся под пользователем. Проверяем, что авторизация прошла успешно.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        String accessToken = responseLogin.extract().path("accessToken");
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());

        // Создаем заказы, используя токен авторизованного пользователя.
        List<Order> orders = OrderGenerator.getOrderList(orderClient);
        ValidatableResponse responseOrder1 = orderClient.createOrderWithToken(orders.get(0), accessToken);
        ValidatableResponse responseOrder2 = orderClient.createOrderWithToken(orders.get(1), accessToken);

        // Получаем список заказов пользователя.
        ValidatableResponse responseGetOrders = orderClient.getOrders(accessToken);

        // Проверяем, что получение списка заказов прошло успешно, статус-код = 200.
        int statusCode = responseGetOrders.extract().statusCode();
        Assert.assertEquals("Status code should be equal to " + SC_OK, SC_OK, statusCode);

        // Проверяем, что success = true.
        boolean areOrdersGot = responseGetOrders.extract().path("success");
        Assert.assertTrue("Value should be equal to true", areOrdersGot);

        // Проверяем, чтo id первого заказа в списке совпадает с id первого заказа пользователя.
        String order1Id = responseGetOrders.extract().path("orders._id[0]");
        String expectedId1 = responseOrder1.extract().path("order._id");
        Assert.assertEquals("The first order's id in the order list should be equal to " + expectedId1,
                expectedId1, order1Id);

        // Проверяем, чтo id второго заказа в списке совпадает с id второго заказа пользователя.
        String order2Id = responseGetOrders.extract().path("orders._id[1]");
        String expectedId2 = responseOrder2.extract().path("order._id");
        Assert.assertEquals("The second order's id in the order list should be equal to " + expectedId2,
                expectedId2, order2Id);

        // Проверяем, чтo общее количество заказов = 2.
        int actualTotal = responseGetOrders.extract().path("total");
        int total = 2;
        Assert.assertEquals("The total number of orders should be equal to " +  total, total, actualTotal);

        // Проверяем, чтo количество заказов за день = 2.
        int actualTotalToday = responseGetOrders.extract().path("totalToday");
        int totalToday = 2;
        Assert.assertEquals("The number of today's orders should be equal to " +  totalToday,
                totalToday, actualTotalToday);
    }

    @Test
    @Tag("Negative")
    @DisplayName("An unauthorized user cannot get a list of his orders")
    @Description("Проверяет, что неавторизованный пользователь не может получить список своих заказов. " +
            "Запрос возвращает ошибку 401 и соответствующий текст, поле \"success\" = false.")
    public void unauthUserCanNotGetHisOrders() {

        // Авторизуемся под пользователем. Проверяем, что авторизация прошла успешно.
        ValidatableResponse responseLogin = userClient.loginUser(Credentials.from(user));
        String accessToken = responseLogin.extract().path("accessToken");
        Assert.assertEquals("Login status code should be equal to " + SC_OK,
                SC_OK, responseLogin.extract().statusCode());
        String refreshToken = responseLogin.extract().path("refreshToken");

        // Создаем заказ, используя токен авторизованного пользователя.
        Order order = OrderGenerator.getNewOrder(orderClient);
        ValidatableResponse responseOrder = orderClient.createOrderWithToken(order, accessToken);

        // Выходим из аккаунта пользователя.
        UserLogout userLogout = new UserLogout(refreshToken);
        ValidatableResponse responseLogout = userClient.logoutUser(userLogout);

        // Получаем список заказов, используя токен не авторизованного пользователя (вышедшего из системы).
        ValidatableResponse responseGetOrders = orderClient.getOrders(accessTokenCreate);

        // Проверяем, что список заказов не получен, статус-код = 401.
        int statusCode = responseGetOrders.extract().statusCode();
        Assert.assertEquals("An unauthorized user should not get a list of his orders. " +
                "Status code should be equal to " + SC_UNAUTHORIZED, SC_UNAUTHORIZED, statusCode);

        // Проверяем, что success = false.
        boolean areOrdersGot = responseGetOrders.extract().path("success");
        Assert.assertFalse("Value should be equal to false", areOrdersGot);

        // Проверяем текст сообщения.
        String actualMessage = responseGetOrders.extract().path("message");
        String message = "You should be authorised";
        Assert.assertEquals("Message should be equal to \"" + message + "\"", message, actualMessage);
    }

}

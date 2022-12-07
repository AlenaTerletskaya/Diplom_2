package order.create_order;

import clients.OrderClient;
import data.OrderGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.ValidatableResponse;
import models.order.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;

// Класс с тестами по созданию заказа без токена
public class OrderCreateWithoutTokenTest {

        private OrderClient orderClient;

        @Before
        public void setUp() {
            orderClient = new OrderClient();
        }

        @Test
        @Tag("Positive")
        @DisplayName("A pre-order without a token can be created")
        @Description("Проверяет формирование заказа без передачи токена (без создания и авторизации пользователя)." +
                "Успешный запрос возвращает код 200, имя и номер заказа, поле \"success\" = true. ")
        public void preOrderWithoutTokenCanBeCreated() {

            // Создаем новый заказ без токена.
            Order newOrder = OrderGenerator.getNewOrder(orderClient);
            ValidatableResponse responseOrder = orderClient.createOrderNoToken(newOrder);

            // Проверяем, что создание заказа прошло успешно.
            int statusCode = responseOrder.extract().statusCode();
            Assert.assertEquals("Status code should be equal to " + SC_OK, SC_OK, statusCode);

            // Проверяем, что success = true.
            boolean isOrderCreated = responseOrder.extract().path("success");
            Assert.assertTrue("Value should be equal to true", isOrderCreated);

            // Проверяем, чтo имя заказа не null.
            String orderName = responseOrder.extract().path("name");
            Assert.assertNotNull("The order name should not be null" , orderName);

            // Проверяем, чтo номер заказа не null.
            int orderNumber = responseOrder.extract().path("order.number");
            Assert.assertNotNull("The order number should not be null" , orderNumber);
        }
}

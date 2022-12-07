package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.order.Order;
import models.order.ingredients.Ingredients;

import static io.restassured.RestAssured.given;

// Класс-клиент для отправки запросов к эндпойнтам по действиям с заказами.
public class OrderClient extends Client {

    private static final String PATH_CREATE_ORDER = "api/orders";
    private static final String PATH_INGREDIENTS = "api/ingredients";

    // Метод отправляет запрос на создание заказа без токена и возвращает ответ.
    @Step("Send POST request to create an order without token to api/orders")
    public ValidatableResponse createOrderNoToken(Order order) {
        return given()
                .spec(getRequestSpec())
                .body(order)
                .when()
                .post(PATH_CREATE_ORDER)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на создание заказа с токеном и возвращает ответ.
    @Step("Send POST request to create an order with token to api/orders")
    public ValidatableResponse createOrderWithToken(Order order, String accessToken) {
        return given()
                .spec(getRequestSpec())
                .auth().oauth2(accessToken.substring(7))
                .body(order)
                .when()
                .post(PATH_CREATE_ORDER)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на получение ингредиентов и возвращает ответ в виде экземпляра класса Ingredients.
    @Step("Send GET request to receive ingredients to api/ingredients")
    public Ingredients getIngredients() {
        return given()
                .spec(getRequestSpec())
                .when()
                .get(PATH_INGREDIENTS)
                .then()
                .spec(getResponseSpec())
                .extract()
                .body()
                .as(Ingredients.class);
    }


// Получить заказы конкретного пользователя
//GET https://stellarburgers.nomoreparties.site/api/orders
//При успешном подключении бэкенд вернёт максимум 50 последних заказов
//пользователя. Они сортируются по времени обновления.

    //Сервер не возвращает поля owner : при сокет-соединении с персональной лентой
    //заказов нужно предоставить серверу авторизационный токен.
    //Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized.

    // Метод отправляет запрос на получение заказов пользователя и возвращает ответ.
    @Step("Send GET request to receive user's orders to api/orders")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(getRequestSpec())
                .auth().oauth2(accessToken.substring(7))
                .when()
                .get(PATH_CREATE_ORDER)
                .then()
                .spec(getResponseSpec());
    }

}


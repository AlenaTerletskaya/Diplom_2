package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.user.Credentials;
import models.user.User;
import models.user.UserLogout;

import static io.restassured.RestAssured.given;

// Класс-клиент для отправки запросов к эндпойнтам по действиям с пользователем.
public class UserClient extends Client {

    private static final String PATH_CREATE = "api/auth/register";
    private static final String PATH_LOGIN = "api/auth/login";
    private static final String PATH_CHANGE_DELETE = "api/auth/user";
    private static final String PATH_LOGOUT = "api/auth/logout";

    // Метод отправляет запрос на создание уникального пользователя и возвращает ответ.
    @Step("Send POST request to create a unique courier to api/auth/register")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getRequestSpec())
                .body(user)
                .when()
                .post(PATH_CREATE)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на авторизацию пользователя и возвращает ответ.
    @Step("Send POST request for authorization of the user to api/auth/login")
    public ValidatableResponse loginUser(Credentials credentials) {
        return given()
                .spec(getRequestSpec())
                .body(credentials)
                .when()
                .post(PATH_LOGIN)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на удаление пользователя и возвращает ответ.
    @Step("Send DELETE request to remove the user to api/auth/user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getRequestSpec())
                .auth().oauth2(accessToken.substring(7))
                .when()
                .delete(PATH_CHANGE_DELETE)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на изменение данных пользователя и возвращает ответ.
    @Step("Send PATCH request to change a user data to api/auth/user")
    public ValidatableResponse changeUser(User changedUser, String accessToken) {
        return given()
                .spec(getRequestSpec())
                .auth().oauth2(accessToken.substring(7))
                .body(changedUser)
                .when()
                .patch(PATH_CHANGE_DELETE)
                .then()
                .spec(getResponseSpec());
    }

    // Метод отправляет запрос на выход пользователя из системы и возвращает ответ.
    @Step("Send POST request for log out of the user to api/auth/logout")
    public ValidatableResponse logoutUser(UserLogout userLogout) {
        return given()
                .spec(getRequestSpec())
                .body(userLogout)
                .when()
                .post(PATH_LOGOUT)
                .then()
                .spec(getResponseSpec());
    }
}

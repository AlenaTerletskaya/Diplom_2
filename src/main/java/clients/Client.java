package clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

// Класс для формирования настроек запроса
public class Client {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    // Метод возвращает спецификацию (настройки) запроса
    protected RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .log(LogDetail.ALL)
                .build();
    }

    // Метод возвращает спецификацию (настройки) ответа
    protected ResponseSpecification getResponseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

}

package ru.yandex.practicum;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.yandex.practicum.model.Order;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class OrderClient extends BaseApiClient {

    public static Response createOrder(Order order) {

        return given()
                .spec(getRecSpec())
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(BASE_URL + "/api/v1/orders");

    }

    public static Boolean isFreeOrdersThere() {

        return given()
                .spec(getRecSpec())
                .when()
                .get(BASE_URL + "/api/v1/orders?limit=10")
                .then().log().all()
                .extract()
                .body()
                .jsonPath()
                .getList("orders")
                .isEmpty();

    }

    public static Boolean deleteOrder(Order order) {

        return given()
                .spec(getRecSpec())
                .contentType(ContentType.JSON)
                .body(order.getTrack())
                .when()
                .put(BASE_URL + "/api/v1/orders/cancel")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("ok");

    }
}

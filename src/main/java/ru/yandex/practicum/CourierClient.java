package ru.yandex.practicum;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.yandex.practicum.model.Courier;
import ru.yandex.practicum.model.CourierCredentials;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierClient extends BaseApiClient {

    public static Response createCourier(Courier courier){

        return given()
                .spec(getRecSpec())
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post(BASE_URL + "/api/v1/courier/");


    }

    public static Response loginCourier(CourierCredentials courierCredentials) {

        return given()
                .spec(getRecSpec())
                .contentType(ContentType.JSON)
                .body(courierCredentials)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

    }

    public static Boolean deleteCourier(int courierId) {

        return given()
                .spec(getRecSpec())
                .when()
                .delete(BASE_URL + "/api/v1/courier/" + courierId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("ok");

    }

}

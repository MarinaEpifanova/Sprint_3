package ru.yandex.practicum;

import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.model.Courier;
import ru.yandex.practicum.model.CourierCredentials;
import ru.yandex.practicum.model.CreateCourierResponse;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.practicum.CourierClient.*;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;


@Story("API для создания курьеров")
public class CreateCourierTest {

    Courier courier;
    CourierCredentials courierCredentials;
    Response responseCreate;
    Boolean deleteCourier = true;
    int courierId = 0;

    @Before
    public void beforeTests() {
        courier = Courier.getRandomCourier();
    }

    @After
    public void afterTests() {
        if (deleteCourier) {
            if (courierId == 0) {
                courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
                courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
            }
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Создание курьера")
    @Description("Проверяем:\n" +
            " - курьера можно создать\n" +
            " - запрос создания курьера возвращает правильный код ответа\n" +
            " - успешный запрос возвращает ok: true\n")
    public void courierTestNormalCreate() {
        responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("Проверяем:\n" +
            " - нельзя создать двух одинаковых курьеров")
    public void courierTestDoubleCreate() {
        Response responseCreateFirst = createCourier(courier);
        assertEquals(SC_CREATED, responseCreateFirst.statusCode());
        CreateCourierResponse createCourierResponse = responseCreateFirst.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        Response responseCreateSecond = createCourier(courier);
        assertEquals(SC_CONFLICT, responseCreateSecond.statusCode());
    }

    @Test
    @DisplayName("Создание курьера с повторяющимся логином")
    @Description("Проверяем:\n" +
            " - нельзя создать двух курьеров с одинаковым логином")
    public void courierTestDoubleLogin() {
        Response responseCreateFirst = createCourier(courier);
        assertEquals(SC_CREATED, responseCreateFirst.statusCode());
        CreateCourierResponse createCourierResponse = responseCreateFirst.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        Courier courierSameLogin = Courier.getRandomCourier();
        courierSameLogin.setLogin(courier.getLogin());

        Response responseCreateSecond = createCourier(courierSameLogin);
        assertEquals(SC_CONFLICT, responseCreateSecond.statusCode());
    }

    @Test
    @DisplayName("Создание курьера при пустом логине")
    @Description("Проверяем:\n" +
            " - если логина нет, запрос возвращает ошибку")
    public void courierTestMissingLogin() {
        courier.setLogin("");
        responseCreate = createCourier(courier);
        assertTrue(responseCreate.statusCode() != SC_CREATED);
        deleteCourier = false;
    }

    @Test
    @DisplayName("Создание курьера при пустом пароле")
    @Description("Проверяем:\n" +
            " - если пароля нет, запрос возвращает ошибку")
    public void courierTestMissingPassword() {
        courier.setPassword("");
        Response responseCreateFirst = createCourier(courier);
        assertTrue(responseCreateFirst.statusCode() != SC_CREATED);
        deleteCourier = false;
    }

    @Test
    @DisplayName("Создание курьера при пустом имени")
    @Description("Проверяем:\n" +
            " - если имени нет, курьер создается")
    public void courierTestMissingFirstName() {
        courier.setFirstName("");
        responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);
    }

}

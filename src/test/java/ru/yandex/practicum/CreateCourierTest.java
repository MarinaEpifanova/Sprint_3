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


@Story("API для работы с курьерами")
public class CourierTest {

    Courier courier;
    int courierId = 0;

    @Before
    public void beforeTests() {
        courier = Courier.getRandomCourier();
    }

    @After
    public void afterTests() {
        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }


    @Test
    @DisplayName("Создание и логин курьера")
    @Description("Проверяем:\n" +
            " - курьера можно создать\n" +
            " - запрос создания курьера возвращает правильный код ответа\n" +
            " - успешный запрос возвращает ok: true\n" +
            " - курьер может авторизоваться")
    public void courierTestNormalCreate() {

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = loginCourier(courierCredentials);
        assertEquals(SC_OK, responseLogin.statusCode());

        courierId = responseLogin.body().jsonPath().getInt("id");
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

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = loginCourier(courierCredentials);
        assertEquals(SC_OK, responseLogin.statusCode());

        courierId = responseLogin.body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Создание курьера с повторяющимся логином")
    @Description("Проверяем:\n" +
            " - нельзя создать двух одинаковых курьеров")
    public void courierTestDoubleLogin() {

        Response responseCreateFirst = createCourier(courier);
        assertEquals(SC_CREATED, responseCreateFirst.statusCode());
        CreateCourierResponse createCourierResponse = responseCreateFirst.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        Courier courierSameLogin = Courier.getRandomCourier();
        courierSameLogin.setLogin(courier.getLogin());

        Response responseCreateSecond = createCourier(courierSameLogin);
        assertEquals(SC_CONFLICT, responseCreateSecond.statusCode());

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = loginCourier(courierCredentials);
        assertEquals(SC_OK, responseLogin.statusCode());

        courierId = responseLogin.body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Создание курьера при пустом логине")
    @Description("Проверяем:\n" +
            " - если логина нет, запрос возвращает ошибку")
    public void courierTestMissingLogin() {
        courier.setLogin("");
        Response responseCreateFirst = createCourier(courier);
        assertTrue(responseCreateFirst.statusCode() != SC_CREATED);
    }

    @Test
    @DisplayName("Создание курьера при пустом пароле")
    @Description("Проверяем:\n" +
            " - если пароля нет, запрос возвращает ошибку")
    public void courierTestMissingPassword() {
        courier.setPassword("");
        Response responseCreateFirst = createCourier(courier);
        assertTrue(responseCreateFirst.statusCode() != SC_CREATED);
    }

    @Test
    @DisplayName("Создание курьера при пустом имени")
    @Description("Проверяем:\n" +
            " - если имени нет, курьер создается")
    public void courierTestMissingFirstName() {
        courier.setFirstName("");

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        Response responseLogin = loginCourier(courierCredentials);
        assertEquals(SC_OK, responseLogin.statusCode());

        courierId = responseLogin.body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин курьера с ошибкой в логине")
    @Description("Проверяем:\n" +
            " - неправильно указан логин")
    public void courierTestLoginIncorrectLogin() {

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin() + RandomStringUtils.randomAlphabetic(1),
                courier.getPassword());
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);

        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин курьера с ошибкой в пароле")
    @Description("Проверяем:\n" +
            " - неправильно указан пароль")
    public void courierTestLoginIncorrectPassword() {

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(),
                courier.getPassword() + RandomStringUtils.randomAlphabetic(1));
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);

        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин курьера с пустым логином")
    @Description("Проверяем:\n" +
            " - логин не указан")
    public void courierTestLoginMissingLogin() {

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials("", courier.getPassword());
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);

        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин курьера с пустым паролем")
    @Description("Проверяем:\n" +
            " - пароль не указан")
    public void courierTestLoginMissingPassword() {

        Response responseCreate = createCourier(courier);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        CreateCourierResponse createCourierResponse = responseCreate.as(CreateCourierResponse.class);
        assertTrue(createCourierResponse.ok);

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), "");
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);

        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин несущевствующего пользователя")
    @Description("Проверяем:\n" +
            " - если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void courierTestLoginMissingUser() {

        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);

    }

}

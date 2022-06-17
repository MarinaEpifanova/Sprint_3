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
public class LoginCourierTest {

    Courier courier;
    CourierCredentials courierCredentials;
    Response responseLogin;
    int courierId = 0;

    @Before
    public void beforeTests() {
        courier = Courier.getRandomCourier();
        Response responseCreate = createCourier(courier);
        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
    }

    @After
    public void afterTests() {
        if (courierId == 0) {
           courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
        }
        deleteCourier(courierId);
    }

    @Test
    @DisplayName("Логин курьера")
    @Description("Проверяем:\n" +
            " - курьер может авторизоваться")
    public void courierTestNormalLogin() {
        responseLogin = loginCourier(courierCredentials);
        assertEquals(SC_OK, responseLogin.statusCode());
        courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Логин курьера с ошибкой в логине")
    @Description("Проверяем:\n" +
            " - неправильно указан логин")
    public void courierTestLoginIncorrectLogin() {
        CourierCredentials courierCredentialsCorrupted = new CourierCredentials(courier.getLogin() + RandomStringUtils.randomAlphabetic(1),
                courier.getPassword());
        assertTrue(loginCourier(courierCredentialsCorrupted).statusCode() != SC_OK);
    }

    @Test
    @DisplayName("Логин курьера с ошибкой в пароле")
    @Description("Проверяем:\n" +
            " - неправильно указан пароль")
    public void courierTestLoginIncorrectPassword() {
        CourierCredentials courierCredentialsCorrupted = new CourierCredentials(courier.getLogin(),
                courier.getPassword() + RandomStringUtils.randomAlphabetic(1));
        assertTrue(loginCourier(courierCredentialsCorrupted).statusCode() != SC_OK);
    }

    @Test
    @DisplayName("Логин курьера с пустым логином")
    @Description("Проверяем:\n" +
            " - логин не указан")
    public void courierTestLoginMissingLogin() {
        CourierCredentials courierCredentialsCorrupted = new CourierCredentials("",
                courier.getPassword());
        assertTrue(loginCourier(courierCredentialsCorrupted).statusCode() != SC_OK);
    }

    @Test
    @DisplayName("Логин курьера с пустым паролем")
    @Description("Проверяем:\n" +
            " - пароль не указан")
    public void courierTestLoginMissingPassword() {
        CourierCredentials courierCredentialsCorrupted = new CourierCredentials( courier.getLogin(),"");
        assertTrue(loginCourier(courierCredentialsCorrupted).statusCode() != SC_OK);
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверяем:\n" +
            " - если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void courierTestLoginMissingUser() {
        Courier randomCourier = Courier.getRandomCourier();
        CourierCredentials courierCredentials = new CourierCredentials(randomCourier.getLogin(), randomCourier.getPassword());
        assertTrue(loginCourier(courierCredentials).statusCode() != SC_OK);
    }

}



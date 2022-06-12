package ru.yandex.practicum;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.model.Order;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;
import static ru.yandex.practicum.OrderClient.createOrder;

@Story("API для работы с заказами")
@RunWith(Parameterized.class)
public class CreateOrderParameterizedTest {

    private final String[] color;
    private final int expected = SC_CREATED;

    public CreateOrderParameterizedTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Проверяем:\n" +
            " - можно создать заказ с разными наборами цветов\n" +
            " - тело ответа содержит track")
    public void ordersTestNormalCreate() {
        Order order = Order.getRandomOrders();
        order.setColor(color);

        Response responseCreate = createOrder(order);
        assertEquals(expected, responseCreate.statusCode());
        order.setTrack(responseCreate.body().jsonPath().getString("track"));
    }
}
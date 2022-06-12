package ru.yandex.practicum;

import io.qameta.allure.Story;
import org.junit.Test;
import static org.junit.Assert.*;
import static ru.yandex.practicum.OrderClient.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;



@Story("API для работы со списком заказов")
public class OrdersListTest {


    @Test
    @DisplayName("Список заказов")
    @Description("Проверяем:" +
            " - в тело ответа возвращается список заказов")
    public void ordersListTest() {

        assertFalse(isFreeOrdersThere());

    }
}

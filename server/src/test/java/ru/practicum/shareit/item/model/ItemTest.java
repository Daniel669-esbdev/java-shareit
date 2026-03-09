package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели Item")
class ItemTest {

    @Test
    @DisplayName("Проверка геттеров и сеттеров")
    void testItemGettersAndSetters() {
        User owner = User.builder().id(1L).name("Daniel").build();
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("ноутбук");
        item.setDescription("мощный игровой ноутбук");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        assertAll("Проверка полей вещи",
                () -> assertEquals(1L, item.getId(), "ID не совпадает"),
                () -> assertEquals("ноутбук", item.getName(), "Название не совпадает"),
                () -> assertEquals("мощный игровой ноутбук", item.getDescription(), "Описание не совпадает"),
                () -> assertTrue(item.getAvailable(), "Статус доступности должен быть true"),
                () -> assertEquals(owner, item.getOwner(), "Владелец не совпадает"),
                () -> assertEquals(request, item.getRequest(), "Запрос на вещь не совпадает")
        );
    }

    @Test
    @DisplayName("Проверка работы Builder")
    void testItemBuilder() {
        Item item = Item.builder()
                .id(1L)
                .name("ноутбук Daniel")
                .description("описание ноутбука")
                .available(false)
                .build();

        assertAll("Проверка билдера",
                () -> assertEquals(1L, item.getId()),
                () -> assertEquals("ноутбук Daniel", item.getName()),
                () -> assertFalse(item.getAvailable())
        );
    }

    @Test
    @DisplayName("Проверка toString и исключенных полей")
    void testItemToString() {
        User owner = User.builder().id(1L).name("Daniel").build();
        Item item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .owner(owner)
                .build();

        String result = item.toString();

        assertAll("Проверка содержимого toString",
                () -> assertTrue(result.contains("Item"), "Должно содержать имя класса"),
                () -> assertTrue(result.contains("id=1"), "Должно содержать id"),
                () -> assertTrue(result.contains("name=ноутбук"), "Должно содержать корректное имя"),
                () -> assertFalse(result.contains("owner"), "Не должно содержать owner (Exclude)"),
                () -> assertFalse(result.contains("Daniel"), "Не должно содержать данные владельца")
        );
    }

    @Test
    @DisplayName("Проверка конструкторов")
    void testConstructors() {
        Item emptyItem = new Item();
        assertNull(emptyItem.getId());

        User owner = User.builder().id(1L).build();
        ItemRequest request = new ItemRequest();
        Item fullItem = new Item(1L, "ноутбук", "описание", true, owner, request);

        assertEquals("ноутбук", fullItem.getName());
        assertEquals(owner, fullItem.getOwner());
    }
}
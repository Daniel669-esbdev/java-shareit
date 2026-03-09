package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели ItemRequest")
class ItemRequestTest {

    @Test
    void testItemRequestFields() {
        User requester = User.builder().id(1L).name("Daniel").build();
        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("нужен мощный ноутбук для работы");
        request.setRequestor(requester);
        request.setCreated(now);

        assertAll("Проверка полей запроса",
                () -> assertEquals(1L, request.getId()),
                () -> assertEquals("нужен мощный ноутбук для работы", request.getDescription()),
                () -> assertEquals(requester, request.getRequestor()),
                () -> assertEquals(now, request.getCreated())
        );
    }

    @Test
    void testItemRequestBuilder() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest request = ItemRequest.builder()
                .id(5L)
                .description("ноутбук в аренду")
                .created(now)
                .build();

        assertNotNull(request);
        assertEquals(5L, request.getId());
        assertEquals("ноутбук в аренду", request.getDescription());
        assertEquals(now, request.getCreated());
    }

    @Test
    void testItemRequestEqualsAndHashCode() {
        ItemRequest request1 = ItemRequest.builder().id(1L).build();
        ItemRequest request2 = ItemRequest.builder().id(1L).build();
        ItemRequest request3 = ItemRequest.builder().id(2L).build();

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
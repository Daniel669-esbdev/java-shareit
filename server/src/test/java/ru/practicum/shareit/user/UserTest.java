package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели User")
class UserTest {

    @Test
    @DisplayName("Проверка геттеров, сеттеров и конструктора")
    void testUserFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Daniel");
        user.setEmail("daniel@mail.ru");

        assertAll("Проверка полей пользователя",
                () -> assertEquals(1L, user.getId(), "ID должен совпадать"),
                () -> assertEquals("Daniel", user.getName(), "Имя должно совпадать"),
                () -> assertEquals("daniel@mail.ru", user.getEmail(), "Email должен совпадать")
        );
    }

    @Test
    @DisplayName("Проверка работы Builder")
    void testUserBuilder() {
        User user = User.builder()
                .id(2L)
                .name("Daniel")
                .email("daniel_admin@mail.ru")
                .build();

        assertAll("Проверка сборки объекта через Builder",
                () -> assertNotNull(user, "Объект не должен быть null"),
                () -> assertEquals(2L, user.getId(), "ID в билдере установлен неверно"),
                () -> assertEquals("Daniel", user.getName(), "Имя в билдере установлено неверно"),
                () -> assertEquals("daniel_admin@mail.ru", user.getEmail(), "Email в билдере установлен неверно")
        );
    }
}
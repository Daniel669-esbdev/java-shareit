package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели Comment")
class CommentTest {

    @Test
    @DisplayName("Проверка работы геттеров и сеттеров")
    void testCommentGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        User author = User.builder().id(1L).name("Автор").build();
        Item item = Item.builder().id(1L).name("Вещь").build();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отличная вещь!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);

        assertAll("Проверка всех полей объекта",
                () -> assertEquals(1L, comment.getId(), "ID должен совпадать"),
                () -> assertEquals("Отличная вещь!", comment.getText(), "Текст комментария должен совпадать"),
                () -> assertEquals(author, comment.getAuthor(), "Автор должен совпадать"),
                () -> assertEquals(item, comment.getItem(), "Объект вещи должен совпадать"),
                () -> assertEquals(now, comment.getCreated(), "Дата создания должна совпадать")
        );
    }

    @Test
    @DisplayName("Проверка создания объекта через Builder")
    void testCommentBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Текст из билдера")
                .created(now)
                .build();

        assertAll("Проверка полей, установленных через билдер",
                () -> assertEquals(1L, comment.getId()),
                () -> assertEquals("Текст из билдера", comment.getText()),
                () -> assertEquals(now, comment.getCreated())
        );
    }

    @Test
    @DisplayName("Проверка конструктора без аргументов")
    void testNoArgsConstructor() {
        Comment comment = new Comment();

        assertAll("Поля нового объекта должны быть пустыми",
                () -> assertNull(comment.getId(), "ID должен быть null"),
                () -> assertNull(comment.getText(), "Текст должен быть null")
        );
    }
}
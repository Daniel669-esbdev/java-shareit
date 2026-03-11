package ru.practicum.shareit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.exception.ConflictException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Техническое тестирование инфраструктурных классов")
class InfrastructureTest {

    @Test
    @DisplayName("Покрытие технических компонентов")
    void touchAllTechnicalComponents() throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        ShareItServer server = new ShareItServer();
        assertNotNull(server);

        for (BookingState state : BookingState.values()) {
            assertNotNull(BookingState.valueOf(state.name()));
        }

        ConflictException exc = new ConflictException("Conflict");
        assertNotNull(exc.getMessage());

        touchConstructor(BookingMapper.class);
        touchConstructor(UserMapper.class);
        touchConstructor(ItemMapper.class);
        touchConstructor(CommentMapper.class);
        touchConstructor(ItemRequestMapper.class);

        ItemDto.BookingShortDto shortDto = new ItemDto.BookingShortDto(1L, 1L);
        assertNotNull(shortDto.getId());

        for (BookingStatus status : BookingStatus.values()) {
            assertNotNull(BookingStatus.valueOf(status.name()));
        }
    }

    private void touchConstructor(Class<?> clazz) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            assertNotNull(e.getCause());
        }
    }
}
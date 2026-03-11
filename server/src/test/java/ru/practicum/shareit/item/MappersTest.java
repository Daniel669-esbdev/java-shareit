package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.booking.BookingMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class MappersTest {

    @Test
    void testUtilityClassConstructors() throws Exception {
        Class<?>[] classes = {
                ItemMapper.class,
                UserMapper.class,
                ItemRequestMapper.class,
                CommentMapper.class,
                BookingMapper.class
        };

        for (Class<?> clazz : classes) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (InvocationTargetException e) {
                if (!(e.getCause() instanceof UnsupportedOperationException)) {
                    throw e;
                }
            } catch (InstantiationException | IllegalAccessException e) {
            }
        }
    }
}
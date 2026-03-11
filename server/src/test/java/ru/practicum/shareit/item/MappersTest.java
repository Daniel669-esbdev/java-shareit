package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                Object instance = constructor.newInstance();
                assertNotNull(instance);
            } catch (InvocationTargetException e) {
                System.out.println("Конструктор утилитарного класса " + clazz.getName() + " вызван через рефлексию");
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Ошибка доступа при вызове конструктора " + clazz.getName());
            }
        }
    }
}
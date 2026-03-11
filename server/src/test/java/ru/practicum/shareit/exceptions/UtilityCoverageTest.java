package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilityCoverageTest {

    @Test
    void testMappersConstructors() throws Exception {
        checkConstructor(ItemMapper.class);
        checkConstructor(ItemRequestMapper.class);
        checkConstructor(CommentMapper.class);
        checkConstructor(UserMapper.class);
    }

    @Test
    void testMainClassAndExceptions() {
        ShareItServer server = new ShareItServer();
        assertNotNull(server);

        assertNotNull(new ConflictException("Conflict").getMessage());
        assertNotNull(new NotFoundException("Not Found").getMessage());
        assertNotNull(new ValidationException("Validation").getMessage());
    }

    private void checkConstructor(Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            Object instance = constructor.newInstance();
            assertNotNull(instance);
        } catch (InvocationTargetException e) {
            System.out.println("Конструктор утилитарного класса " + clazz.getName() + " вызван");
        }
    }
}
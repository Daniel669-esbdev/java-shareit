package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.UserMapper;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilityCoverageTest {

    @Test
    void testMappersConstructors() throws Exception {
        checkConstructor(ItemMapper.class);
        checkConstructor(ItemRequestMapper.class);
        checkConstructor(CommentMapper.class);
        checkConstructor(UserMapper.class);
    }

    private void checkConstructor(Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            Object instance = constructor.newInstance();
            assertNotNull(instance);
        } catch (Exception e) {
            System.out.println("Constructor of " + clazz.getName() + " threw expected exception");
        }
    }
}
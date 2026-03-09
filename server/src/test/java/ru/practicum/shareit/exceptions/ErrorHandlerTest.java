package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFoundException() {
        NotFoundException e = new NotFoundException("Not found");
        Map<String, String> response = handler.handleNotFound(e);

        assertThat(response).isNotNull();
        assertThat(response.get("error")).isEqualTo("Not found");
    }

    @Test
    void handleConflictException() {
        ConflictException e = new ConflictException("Conflict");
        Map<String, String> response = handler.handleConflict(e);

        assertThat(response).isNotNull();
        assertThat(response.get("error")).isEqualTo("Conflict");
    }

    @Test
    void handleValidationException() {
        ValidationException e = new ValidationException("Validation failed");
        Map<String, String> response = handler.handleValidation(e);

        assertThat(response).isNotNull();
        assertThat(response.get("error")).isEqualTo("Validation failed");
    }

    @Test
    void handleThrowable() {
        Throwable e = new Throwable("Internal error");
        Map<String, String> response = handler.handleThrowable(e);

        assertThat(response).isNotNull();
        assertThat(response.get("error")).isEqualTo("Произошла непредвиденная ошибка");
        assertThat(response).doesNotContainKey("message");
    }
}
package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleNotFoundException() {
        Map<String, String> response = handler.handleNotFound(new NotFoundException("404"));
        assertThat(response.get("error")).isEqualTo("404");
    }

    @Test
    void handleDataIntegrityViolation() {
        Map<String, String> response = handler.handleDataIntegrity(new DataIntegrityViolationException("Conflict"));
        assertThat(response.get("error")).isEqualTo("Конфликт целостности данных");
    }

    @Test
    void handleConflictException() {
        Map<String, String> response = handler.handleConflict(new ConflictException("409"));
        assertThat(response.get("error")).isEqualTo("409");
    }

    @Test
    void handleValidationException() {
        Map<String, String> response = handler.handleValidation(new ValidationException("400"));
        assertThat(response.get("error")).isEqualTo("400");
    }

    @Test
    void handleUnknownState() {
        MethodArgumentTypeMismatchException mockEx = mock(MethodArgumentTypeMismatchException.class);
        when(mockEx.getName()).thenReturn("state");
        when(mockEx.getValue()).thenReturn("UNSUPPORTED");

        Map<String, String> response = handler.handleUnknownState(mockEx);
        assertThat(response.get("error")).isEqualTo("Unknown state: UNSUPPORTED");
    }

    @Test
    void handleMissingHeader() {
        MissingRequestHeaderException mockEx = mock(MissingRequestHeaderException.class);
        when(mockEx.getHeaderName()).thenReturn("X-Sharer-User-Id");

        Map<String, String> response = handler.handleMissingHeader(mockEx);
        assertThat(response.get("error")).contains("X-Sharer-User-Id");
    }

    @Test
    void handleThrowable() {
        Map<String, String> response = handler.handleThrowable(new Throwable("500"));
        assertThat(response.get("error")).isEqualTo("Произошла непредвиденная ошибка");
    }
}
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
        @Valid
        @RequestBody BookingDto bookingDto) {
        log.info("Запрос на создание бронирования от пользователя ID: {}", userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId,
        @RequestParam Boolean approved) {
        log.info("Запрос на подтверждение бронирования ID: {} от пользователя ID: {}", bookingId, userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId) {
        log.info("Запрос бронирования ID: {} от пользователя ID: {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос всех бронирований пользователя ID: {} со статусом: {}", userId, state);
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований для владельца ID: {} со статусом: {}", userId, state);
        return bookingService.getAllByOwner(userId, state);
    }
}
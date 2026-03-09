package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
        @Valid @RequestBody BookingDto bookingDto) {
        log.info("Creating booking for user ID: {}", userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId,
        @RequestParam(name = "approved") Boolean approved) {
        log.info("Approving booking ID: {} by user ID: {}", bookingId, userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long bookingId) {
        log.info("Getting booking ID: {} for user ID: {}", bookingId, userId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting all bookings for booker ID: {} with state: {}", userId, state);
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting all bookings for owner ID: {} with state: {}", userId, state);
        return bookingService.getAllByOwner(userId, state);
    }
}
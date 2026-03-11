package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingEntityTest {

    @Test
    void testBookingStatusEnum() {
        for (BookingStatus status : BookingStatus.values()) {
            BookingStatus s = BookingStatus.valueOf(status.name());
            assertEquals(status, s);
        }
    }

    @Test
    void testBookingDto() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto dto = BookingDto.builder()
                .id(1L)
                .itemId(2L)
                .start(start)
                .end(end)
                .item(ItemDto.builder().id(2L).build())
                .booker(UserDto.builder().id(3L).build())
                .status(BookingStatus.WAITING)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getItemId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertNotNull(dto.getItem());
        assertNotNull(dto.getBooker());
        assertEquals(BookingStatus.WAITING, dto.getStatus());

        BookingDto emptyDto = new BookingDto();
        emptyDto.setId(10L);
        assertEquals(10L, emptyDto.getId());
    }

    @Test
    void testBookingEntity() {
        User booker = User.builder().id(1L).build();
        LocalDateTime now = LocalDateTime.now();

        Booking booking = Booking.builder()
                .id(1L)
                .start(now)
                .end(now.plusHours(1))
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        assertEquals(1L, booking.getId());
        assertEquals(now, booking.getStart());
        assertEquals(booker, booking.getBooker());

        Booking empty = new Booking();
        empty.setStatus(BookingStatus.REJECTED);
        assertEquals(BookingStatus.REJECTED, empty.getStatus());
    }
}
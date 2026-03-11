package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingStatusTest {
    @Test
    void testBookingStatusValues() {
        for (BookingStatus status : BookingStatus.values()) {
            assertEquals(status, BookingStatus.valueOf(status.name()));
        }
    }
}
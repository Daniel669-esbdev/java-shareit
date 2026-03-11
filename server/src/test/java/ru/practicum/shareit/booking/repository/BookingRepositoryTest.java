package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("owner").email("owner@mail.com").build());
        booker = userRepository.save(User.builder().name("booker").email("booker@mail.com").build());
        item = itemRepository.save(Item.builder().name("ноутбук").description("мощный").available(true).owner(owner).build());

        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> result = bookingRepository.findAllByItemOwnerId(
                owner.getId(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals("ноутбук", result.get(0).getItem().getName());
    }

    @Test
    void findAllByBookerId() {
        List<Booking> result = bookingRepository.findAllByBookerId(
                booker.getId(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, result.size());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStatus(
                owner.getId(),
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, result.size());
        assertEquals(BookingStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStatus(
                booker.getId(),
                BookingStatus.WAITING,
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByBookerIdAndEndBefore(
                booker.getId(),
                LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, result.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartAfter() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                owner.getId(),
                LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertEquals(1, result.size());
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
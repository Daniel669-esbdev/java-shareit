package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Owner").build();
        booker = User.builder().id(2L).name("Booker").build();
        item = Item.builder().id(1L).name("Item").available(true).owner(owner).build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void create_Success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.create(2L, bookingDto);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void create_WhenItemNotAvailable_ThenThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingDto));
    }

    @Test
    void create_WhenBookerIsOwner_ThenThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void create_WhenDatesInvalid_ThenThrowValidationException() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(2L, bookingDto));
    }

    @Test
    void approve_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.approve(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approve_WhenNotOwner_ThenThrowNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approve(2L, 1L, true));
    }

    @Test
    void approve_WhenStatusNotWaiting_ThenThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void getById_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(2L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_WhenAccessDenied_ThenThrowNotFoundException() {
        User otherUser = User.builder().id(3L).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(3L, 1L));
    }

    @Test
    void getAllByBooker_WithDifferentStates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        assertFalse(bookingService.getAllByBooker(2L, "ALL").isEmpty());
        assertFalse(bookingService.getAllByBooker(2L, "CURRENT").isEmpty());
        assertFalse(bookingService.getAllByBooker(2L, "PAST").isEmpty());
        assertFalse(bookingService.getAllByBooker(2L, "FUTURE").isEmpty());
        assertFalse(bookingService.getAllByBooker(2L, "WAITING").isEmpty());
        assertFalse(bookingService.getAllByBooker(2L, "REJECTED").isEmpty());
        assertThrows(ValidationException.class, () -> bookingService.getAllByBooker(2L, "INVALID"));
    }

    @Test
    void getAllByOwner_WithDifferentStates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(anyLong(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(List.of(booking));

        assertFalse(bookingService.getAllByOwner(1L, "ALL").isEmpty());
        assertFalse(bookingService.getAllByOwner(1L, "CURRENT").isEmpty());
        assertFalse(bookingService.getAllByOwner(1L, "PAST").isEmpty());
        assertFalse(bookingService.getAllByOwner(1L, "FUTURE").isEmpty());
        assertFalse(bookingService.getAllByOwner(1L, "WAITING").isEmpty());
        assertFalse(bookingService.getAllByOwner(1L, "REJECTED").isEmpty());
        assertThrows(ValidationException.class, () -> bookingService.getAllByOwner(1L, "INVALID"));
    }
}
package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("User").email("user@mail.com").build();
        item = Item.builder().id(1L).name("Item").description("Desc").available(true).owner(user).build();
        itemDto = ItemDto.builder().name("Item").description("Desc").available(true).build();
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
    }

    @Test
    void update_WhenUserIsNotOwner_ShouldThrowNotFoundException() {
        User otherUser = User.builder().id(2L).build();
        item.setOwner(otherUser);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, itemDto));
    }

    @Test
    void update_WhenFieldsAreNull_ShouldNotUpdateThem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto updateDto = ItemDto.builder().name(null).description(null).available(null).build();

        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertEquals("Item", result.getName());
        assertEquals("Desc", result.getDescription());
    }

    @Test
    void getById_WhenUserIsNotOwner_ShouldNotSeeBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getById(1L, 99L);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        verify(bookingRepository, never()).findAllByItemIdAndStatusNot(anyLong(), any(), any());
    }

    @Test
    void getById_WhenUserIsOwner_ShouldSeeBookings() {
        Booking last = Booking.builder().id(1L).start(LocalDateTime.now().minusDays(1))
                .booker(user).status(BookingStatus.APPROVED).build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndStatusNot(anyLong(), any(), any(Sort.class)))
                .thenReturn(List.of(last));

        ItemDto result = itemService.getById(1L, 1L);

        assertNotNull(result.getLastBooking());
    }

    @Test
    void search_WhenTextIsBlank_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("");
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void createComment_WhenNoBooking_ShouldThrowValidationException() {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                anyLong(), anyLong(), any(), any())).thenReturn(false);

        CommentDto commentDto = CommentDto.builder().text("Text").build();
        assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }
}
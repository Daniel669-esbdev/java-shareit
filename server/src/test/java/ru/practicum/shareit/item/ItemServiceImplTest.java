package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
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
@DisplayName("Тестирование ItemServiceImpl")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Daniel").email("daniel@mail.com").build();
        item = Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDto.builder()
                .name("ноутбук")
                .description("мощный ноутбук")
                .available(true)
                .build();
    }

    @Test
    void create_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        assertEquals("ноутбук", result.getName());
    }

    @Test
    void create_WithRequestId_Success() {
        itemDto.setRequestId(10L);
        ItemRequest request = ItemRequest.builder().id(10L).build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        verify(itemRequestRepository).findById(10L);
    }

    @Test
    void create_WithRequestId_NotFound_ShouldThrowNotFoundException() {
        itemDto.setRequestId(10L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
    }

    @Test
    void update_AllFields_Success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto updateDto = ItemDto.builder()
                .name("новый ноутбук")
                .description("еще мощнее")
                .available(false)
                .build();

        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertEquals("новый ноутбук", result.getName());
        assertEquals("еще мощнее", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void update_WhenNotOwner_ShouldThrowNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.update(99L, 1L, itemDto));
    }

    @Test
    void getById_ByOwner_WithBookings_Success() {
        Booking lastBooking = Booking.builder().id(1L).start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1)).booker(user).build();
        Booking nextBooking = Booking.builder().id(2L).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).booker(user).build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemIdAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        ItemDto result = itemService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals("ноутбук", result.getName());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void getById_NotByOwner_Success() {
        User otherUser = User.builder().id(2L).build();
        Item itemOtherOwner = Item.builder().id(2L).owner(otherUser).build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemOtherOwner));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getById(2L, 1L);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getByOwner_Success() {
        Booking booking = Booking.builder().id(1L).item(item).start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1)).booker(user).build();
        Comment comment = Comment.builder().id(1L).item(item).text("text").build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anyList(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of(comment));

        List<ItemDto> result = itemService.getByOwner(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getLastBooking());
        assertEquals(1, result.get(0).getComments().size());
    }

    @Test
    void search_Success() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("ноутбук");

        assertEquals(1, result.size());
        assertEquals("ноутбук", result.get(0).getName());
    }

    @Test
    void search_EmptyText() {
        assertTrue(itemService.search("").isEmpty());
        assertTrue(itemService.search(null).isEmpty());
    }

    @Test
    void createComment_Success() {
        CommentDto commentDto = CommentDto.builder().text("супер").build();
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Comment comment = Comment.builder()
                .id(1L)
                .text("супер")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = itemService.createComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals("супер", result.getText());
    }

    @Test
    void createComment_WhenNoBooking_ShouldThrowValidationException() {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(false);

        CommentDto commentDto = CommentDto.builder().text("Text").build();
        assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L, commentDto));
    }

    @Test
    void createComment_WhenTextIsEmpty_ShouldThrowValidationException() {
        CommentDto emptyComment = CommentDto.builder().text(" ").build();
        CommentDto nullComment = CommentDto.builder().text(null).build();

        assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L, emptyComment));
        assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L, nullComment));
    }
}
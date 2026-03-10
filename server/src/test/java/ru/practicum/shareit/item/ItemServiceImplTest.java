package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
    void update_Success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto updateDto = ItemDto.builder().name("ноутбук Daniel").build();
        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertEquals("ноутбук Daniel", result.getName());
    }

    @Test
    void update_WhenNotOwner_ShouldThrowNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.update(99L, 1L, itemDto));
    }

    @Test
    void getById_Success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemDto result = itemService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals("ноутбук", result.getName());
    }

    @Test
    void getByOwner_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdInAndStatus(anyList(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getByOwner(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
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
        List<ItemDto> result = itemService.search("");
        assertTrue(result.isEmpty());
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
}
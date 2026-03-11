package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Test Description")
                .build();
    }

    @Test
    void create_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }

    @Test
    void create_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
    }

    @Test
    void getUserRequests_Success() {
        Item item = Item.builder().id(1L).request(itemRequest).build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getAllRequests_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequest_IdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    void getRequestById_InvalidUser_ThrowsNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(99L, 1L));
    }

    @Test
    void getRequestById_InvalidRequest_ThrowsNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 99L));
    }

    @Test
    void getUserRequests_EmptyList_ReturnsEmpty() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void testItemRequestMapperConstructor() throws Exception {
        var constructor = ItemRequestMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (Exception ignored) { }
    }
}
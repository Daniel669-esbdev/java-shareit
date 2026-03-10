package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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

    @Test
    void create_Success() {
        User user = User.builder().id(1L).build();
        ItemRequest request = ItemRequest.builder().id(1L).requestor(user).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto result = itemRequestService.create(1L, new ItemRequestDto());

        assertNotNull(result);
    }

    @Test
    void create_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.create(1L,  new ItemRequestDto()));
    }

    @Test
    void getUserRequests_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertNotNull(result);
    }

    @Test
    void getAllRequests_Success() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

            List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
    }

    @Test
    void getRequestById_Success() {
        ItemRequest request = ItemRequest.builder().id(1L).build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Нужен ноутбук")
                .build();

        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(outputDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужен ноутбук"));
    }

    @Test
    void getUserRequests() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(outputDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllRequests() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(outputDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestDto outputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(outputDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужен ноутбук"));
    }
}
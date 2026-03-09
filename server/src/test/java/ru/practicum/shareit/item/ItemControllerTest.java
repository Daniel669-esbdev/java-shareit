package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemDto inputDto = ItemDto.builder()
                .name("Ноутбук")
                .description("Мощный")
                .available(true)
                .build();

        ItemDto resultDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Мощный")
                .available(true)
                .build();

        when(itemService.create(anyLong(), any()))
                .thenReturn(resultDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ноутбук"));
    }

    @Test
    void update() throws Exception {
        ItemDto inputDto = ItemDto.builder()
                .name("Ноутбук PRO")
                .build();

        ItemDto resultDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук PRO")
                .description("Обновлен")
                .available(true)
                .build();

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(resultDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ноутбук PRO"));
    }

    @Test
    void getById() throws Exception {
        ItemDto resultDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .build();

        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(resultDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ноутбук"));
    }

    @Test
    void getByOwner() throws Exception {
        ItemDto resultDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .build();

        when(itemService.getByOwner(anyLong()))
                .thenReturn(List.of(resultDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ноутбук"));
    }

    @Test
    void search() throws Exception {
        ItemDto resultDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .build();

        when(itemService.search(anyString()))
                .thenReturn(List.of(resultDto));

        mvc.perform(get("/items/search")
                        .param("text", "ноутбук"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Ноутбук"));
    }

    @Test
    void createComment() throws Exception {
        CommentDto inputDto = CommentDto.builder()
                .text("Отличный ноутбук")
                .build();

        CommentDto resultDto = CommentDto.builder()
                .id(1L)
                .text("Отличный ноутбук")
                .authorName("Daniel")
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(resultDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличный ноутбук"));
    }
}
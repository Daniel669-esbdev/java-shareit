package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoTest {

    @Test
    void testItemRequestDto() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemDto> items = new ArrayList<>();

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("test");
        dto.setCreated(now);
        dto.setItems(items);

        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getDescription());
        assertEquals(now, dto.getCreated());
        assertEquals(items, dto.getItems());

        ItemRequestDto dto2 = ItemRequestDto.builder()
                .id(1L)
                .description("test")
                .created(now)
                .items(items)
                .build();

        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
        assertNotNull(dto.toString());

        ItemRequestDto dto3 = new ItemRequestDto(2L, "desc", now, items);
        assertEquals(2L, dto3.getId());
    }
}
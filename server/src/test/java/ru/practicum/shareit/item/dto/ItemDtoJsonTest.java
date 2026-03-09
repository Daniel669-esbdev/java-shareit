package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto.BookingShortDto last = new ItemDto.BookingShortDto(1L, 2L);
        ItemDto.BookingShortDto next = new ItemDto.BookingShortDto(3L, 4L);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Профессиональный инструмент для разработки")
                .available(true)
                .requestId(2L)
                .lastBooking(last)
                .nextBooking(next)
                .comments(List.of())
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ноутбук");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Профессиональный инструмент для разработки");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).hasJsonPathValue("$.lastBooking");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(4);
    }

    @Test
    void testItemDtoDeserialization() throws Exception {
        String content = "{\"id\":1, \"name\":\"Ноутбук\", \"description\":\"Мощный\", \"available\":true, \"requestId\":5}";

        ItemDto result = json.parseObject(content);

        assertThat(result.getName()).isEqualTo("Ноутбук");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Мощный");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isEqualTo(5L);
    }

    @Test
    void testItemDtoDeserializationWithBookings() throws Exception {
        String content = "{\"id\":1, \"lastBooking\":{\"id\":10, \"bookerId\":20}}";

        ItemDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getLastBooking().getId()).isEqualTo(10L);
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(20L);
    }
}
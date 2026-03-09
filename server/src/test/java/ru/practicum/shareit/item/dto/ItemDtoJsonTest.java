package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Профессиональный инструмент для разработки")
                .available(true)
                .requestId(2L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ноутбук");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Профессиональный инструмент для разработки");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }

    @Test
    void testItemDtoDeserialization() throws Exception {
        String content = "{\"id\":1, \"name\":\"Ноутбук\", \"description\":\"Мощный\", \"available\":true}";

        ItemDto result = json.parseObject(content);

        assertThat(result.getName()).isEqualTo("Ноутбук");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAvailable()).isTrue();
    }
}
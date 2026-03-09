package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDto_WhenRequestExists() {
        User owner = User.builder().id(1L).name("Daniel").build();
        ItemRequest request = ItemRequest.builder().id(10L).build();
        Item item = Item.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Мощный")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Ноутбук");
        assertThat(dto.getDescription()).isEqualTo("Мощный");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(10L);
    }

    @Test
    void toItemDto_WhenRequestIsNull() {
        Item item = Item.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Мощный")
                .available(true)
                .request(null)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void toItem() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Телефон")
                .description("Рабочий")
                .available(false)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Телефон");
        assertThat(item.getDescription()).isEqualTo("Рабочий");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void toItemDto_MinimalFields() {
        Item item = Item.builder()
                .id(1L)
                .name("Вещь")
                .available(true)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getName()).isEqualTo("Вещь");
        assertThat(dto.getAvailable()).isTrue();
    }
}
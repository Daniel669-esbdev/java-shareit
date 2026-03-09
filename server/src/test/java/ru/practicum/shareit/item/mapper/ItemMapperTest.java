package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.request.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User owner = User.builder().id(1L).name("Daniel").build();
        ItemRequest request = ItemRequest.builder().id(10L).build();
        Item item = Item.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Игровой")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(10L);
    }

    @Test
    void toItem() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Игровой")
                .available(false)
                .build();

        Item item = ItemMapper.toItem(dto);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo(dto.getName());
        assertThat(item.getDescription()).isEqualTo(dto.getDescription());
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void toItemDtoWithCommentsAndBookings() {
        Item item = Item.builder()
                .id(1L)
                .name("Ноутбук")
                .available(true)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getName()).isEqualTo("Игровой");
    }
}
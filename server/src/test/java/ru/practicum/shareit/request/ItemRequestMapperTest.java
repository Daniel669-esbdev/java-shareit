package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemRequestMapperTest {

    @Test
    void toDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(request.getId());
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toEntity() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Описание")
                .build();

        ItemRequest entity = ItemRequestMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
    }

    @Test
    void testConstructor() throws NoSuchMethodException {
        Constructor<ItemRequestMapper> constructor = ItemRequestMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
}
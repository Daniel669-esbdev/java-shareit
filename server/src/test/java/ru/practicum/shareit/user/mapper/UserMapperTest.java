package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = User.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void toUser() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(dto.getId());
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    void toUserDto_WhenFieldsAreNull() {
        User user = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getEmail()).isNull();
    }
}
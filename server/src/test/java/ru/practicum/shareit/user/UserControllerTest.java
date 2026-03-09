package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void saveNewUser_whenValid_thenStatusOk() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        when(userService.create(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Daniel"))
                .andExpect(jsonPath("$.email").value("daniel@mail.com"));

        verify(userService, times(1)).create(any());
    }

    @Test
    void getUserById_whenExists_thenStatusOk() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        when(userService.getById(1L)).thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Daniel"))
                .andExpect(jsonPath("$.email").value("daniel@mail.com"));
    }

    @Test
    void getUserById_whenNotFound_thenStatusNotFound() throws Exception {
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("User not found"));

        mvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_thenStatusOk() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        when(userService.findAll()).thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Daniel"))
                .andExpect(jsonPath("$[0].email").value("daniel@mail.com"));
    }

    @Test
    void updateUser_whenValid_thenStatusOk() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@mail.com")
                .build();

        UserDto resultDto = UserDto.builder()
                .id(1L)
                .name("Updated Name")
                .email("updated@mail.com")
                .build();

        when(userService.update(anyLong(), any())).thenReturn(resultDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));
    }

    @Test
    void deleteUser_thenStatusOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }
}
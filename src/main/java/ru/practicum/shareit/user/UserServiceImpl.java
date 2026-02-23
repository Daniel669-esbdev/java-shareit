package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public UserDto create(UserDto userDto) {
        checkEmail(userDto.getEmail(), null);
        User user = UserMapper.toUser(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (!userDto.getEmail().contains("@")) {
                throw new ValidationException("Email должен содержать символ '@'");
            }
            checkEmail(userDto.getEmail(), id);
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public List<UserDto> findAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private void checkEmail(String email, Long id) {
        boolean exists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email) && !u.getId().equals(id));
        if (exists) {
            throw new ConflictException("Email " + email + " уже занят");
        }
    }
}
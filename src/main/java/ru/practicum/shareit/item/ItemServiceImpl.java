package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new HashMap<>();
    private final UserService userService;
    private Long idCounter = 1L;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idCounter++);
        item.setOwner(owner);
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с id " + itemId + " не найдена");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getByOwner(Long userId) {
        userService.getById(userId);

        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String query = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(query) || i.getDescription().toLowerCase().contains(query))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    void getByOwner() {
        User owner = User.builder()
                .name("Daniel")
                .email("daniel@mail.com")
                .build();
        em.persist(owner);

        Item item1 = Item.builder()
                .name("Игровой ноутбук")
                .description("RTX 4090")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);

        Item item2 = Item.builder()
                .name("Рабочий ноутбук")
                .description("MacBook Pro")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);

        em.flush();
        em.clear();

        List<ItemDto> items = service.getByOwner(owner.getId());

        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), containsString("ноутбук"));
        assertThat(items.get(1).getName(), containsString("ноутбук"));
    }

    @Test
    void createAndGetById() {
        User owner = User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build();
        em.persist(owner);

        ItemDto itemDto = ItemDto.builder()
                .name("Ультрабук")
                .description("Тонкий и легкий")
                .available(true)
                .build();

        ItemDto created = service.create(owner.getId(), itemDto);
        ItemDto found = service.getById(created.getId(), owner.getId());

        assertThat(found.getName(), equalTo("Ультрабук"));
        assertThat(found.getDescription(), equalTo("Тонкий и легкий"));
    }

    @Test
    void updateItem() {
        User owner = User.builder()
                .name("UpdateOwner")
                .email("update@mail.com")
                .build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Старый ноутбук")
                .description("Медленный")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        ItemDto updateDto = ItemDto.builder()
                .name("Обновленный ноутбук")
                .build();

        service.update(owner.getId(), item.getId(), updateDto);
        ItemDto updated = service.getById(item.getId(), owner.getId());

        assertThat(updated.getName(), equalTo("Обновленный ноутбук"));
        assertThat(updated.getDescription(), equalTo("Медленный"));
    }

    @Test
    void searchItems() {
        User owner = User.builder()
                .name("SearchOwner")
                .email("search@mail.com")
                .build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Ноутбук для программирования")
                .description("16 Гб ОЗУ")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        List<ItemDto> results = service.search("программирования");

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getName(), containsString("Ноутбук"));
    }
}
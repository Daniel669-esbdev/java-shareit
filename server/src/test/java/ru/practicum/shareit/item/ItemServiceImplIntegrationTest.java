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
    void getUserItems() {
        User owner = User.builder()
                .name("Daniel")
                .email("daniel@mail.com")
                .build();
        em.persist(owner);

        Item item1 = Item.builder()
                .name("Ноутбук")
                .description("Мощный")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);

        Item item2 = Item.builder()
                .name("Телефон")
                .description("Рабочий")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);

        em.flush();
        em.clear();

        List<ItemDto> items = service.getByOwner(owner.getId());

        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), equalTo("Ноутбук"));
        assertThat(items.get(1).getName(), equalTo("Телефон"));
    }
}
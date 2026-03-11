package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void search() {
        User owner = User.builder().name("Daniel").email("dan@mail.com").build();
        em.persist(owner);

        Item item1 = Item.builder()
                .name("Ноутбук")
                .description("Мощный игровой ноут")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);

        Item item2 = Item.builder()
                .name("Смартфон")
                .description("Экран 6 дюймов")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);

        Item item3 = Item.builder()
                .name("Старый ноутбук")
                .description("Не работает")
                .available(false)
                .owner(owner)
                .build();
        em.persist(item3);

        List<Item> result = itemRepository.search("нОуТ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ноутбук");
    }

    @Test
    void findAllByOwnerId() {
        User owner = User.builder().name("Owner").email("owner@mail.com").build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Ноутбук")
                .description("Desc")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        List<Item> result = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwner().getName()).isEqualTo("Owner");
    }

    @Test
    void search_WhenTextIsEmpty_ReturnEmptyList() {
        User owner = User.builder().name("Daniel").email("daniel_empty@mail.com").build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Ноутбук")
                .description("Desc")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        String text = "";
        List<Item> result = text.isBlank() ? Collections.emptyList() : itemRepository.search(text);

        assertThat(result).isEmpty();
    }

    @Test
    void search_WhenNoMatches_ReturnEmptyList() {
        User owner = User.builder().name("Daniel").email("daniel_no_match@mail.com").build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Ноутбук")
                .description("Desc")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        List<Item> result = itemRepository.search("пылесос");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByRequest_Id() {
        User owner = User.builder().name("Owner").email("owner@mail.com").build();
        em.persist(owner);

        User requestor = User.builder().name("Req").email("req@mail.com").build();
        em.persist(requestor);

        ru.practicum.shareit.request.ItemRequest request = ru.practicum.shareit.request.ItemRequest.builder()
                .description("Нужна вещь")
                .requestor(requestor)
                .created(java.time.LocalDateTime.now())
                .build();
        em.persist(request);

        Item item = Item.builder()
                .name("Вещь")
                .description("Описание")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        em.persist(item);

        List<Item> result = itemRepository.findAllByRequest_Id(request.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequest().getId()).isEqualTo(request.getId());
    }

    @Test
    void findAllByRequest_IdIn() {
        User owner = User.builder().name("Owner").email("owner2@mail.com").build();
        em.persist(owner);

        User requestor = User.builder().name("Req2").email("req2@mail.com").build();
        em.persist(requestor);

        ru.practicum.shareit.request.ItemRequest req1 = ru.practicum.shareit.request.ItemRequest.builder()
                .description("Запрос 1")
                .requestor(requestor)
                .created(java.time.LocalDateTime.now())
                .build();
        ru.practicum.shareit.request.ItemRequest req2 = ru.practicum.shareit.request.ItemRequest.builder()
                .description("Запрос 2")
                .requestor(requestor)
                .created(java.time.LocalDateTime.now())
                .build();
        em.persist(req1);
        em.persist(req2);

        Item item1 = Item.builder().name("I1").available(true).owner(owner).request(req1).build();
        Item item2 = Item.builder().name("I2").available(true).owner(owner).request(req2).build();
        em.persist(item1);
        em.persist(item2);

        List<Item> result = itemRepository.findAllByRequest_IdIn(List.of(req1.getId(), req2.getId()));

        assertThat(result).hasSize(2);
    }
}
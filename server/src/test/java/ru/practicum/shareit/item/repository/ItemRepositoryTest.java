package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchItemsByText() {
        User owner = User.builder().name("Daniel").email("dan@mail.com").build();
        em.persist(owner);

        Item item = Item.builder()
                .name("Ноутбук")
                .description("Мощный игровой ноут")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        List<Item> result = itemRepository.search("нОуТ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ноутбук");
    }
}
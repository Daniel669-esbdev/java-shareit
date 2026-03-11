package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "spring.main.web-application-type=none",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRequestService service;

    @Test
    void createAndGetById() {
        User requester = User.builder()
                .name("Daniel")
                .email("daniel_create@mail.com")
                .build();
        em.persist(requester);

        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Нужен ноутбук")
                .build();

        ItemRequestDto created = service.create(requester.getId(), inputDto);
        ItemRequestDto found = service.getRequestById(requester.getId(), created.getId());

        assertThat(found.getId(), notNullValue());
        assertThat(found.getDescription(), equalTo("Нужен ноутбук"));
    }

    @Test
    void getUserRequests() {
        User requester = User.builder()
                .name("Daniel")
                .email("daniel_request@mail.com")
                .build();
        em.persist(requester);

        ItemRequest request = ItemRequest.builder()
                .description("Нужен второй ноутбук для разработки")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);

        em.flush();
        em.clear();

        List<ItemRequestDto> results = service.getUserRequests(requester.getId());

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getDescription(), containsString("ноутбук"));
    }

    @Test
    void getAllRequests() {
        User owner = User.builder()
                .name("Owner")
                .email("owner_req@mail.com")
                .build();
        em.persist(owner);

        User requester = User.builder()
                .name("Requester")
                .email("req_req@mail.com")
                .build();
        em.persist(requester);

        ItemRequest request = ItemRequest.builder()
                .description("Ищу мощный ноутбук")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);

        em.flush();
        em.clear();

        List<ItemRequestDto> results = service.getAllRequests(owner.getId(), 0, 10);

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getDescription(), equalTo("Ищу мощный ноутбук"));
    }

    @Test
    void getAllRequests_WhenEmpty() {
        User owner = User.builder()
                .name("Owner Only")
                .email("owner_only@mail.com")
                .build();
        em.persist(owner);

        List<ItemRequestDto> results = service.getAllRequests(owner.getId(), 0, 10);

        assertThat(results, is(empty()));
    }
}
package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService service;

    @Test
    void getUserRequests() {
        User requester = User.builder()
                .name("Daniel")
                .email("daniel_request@mail.com")
                .build();
        em.persist(requester);

        ItemRequest request = ItemRequest.builder()
                .description("Нужен второй монитор для разработки")
                .requestor(requester)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);

        em.flush();
        em.clear();

        List<ItemRequestDto> results = service.getUserRequests(requester.getId());

        assertThat(results, hasSize(1));
        assertThat(results.get(0).getDescription(), containsString("монитор"));
    }
}
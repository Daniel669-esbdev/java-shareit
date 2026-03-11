package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService service;
    private final EntityManager em;

    @Test
    void create() {
        UserDto userDto = UserDto.builder()
                .name("Daniel")
                .email("daniel@mail.com")
                .build();

        service.create(userDto);

        User result = em.createQuery("Select u from User u where u.email = :email", User.class)
                .setParameter("email", "daniel@mail.com")
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Daniel"));
        assertThat(result.getEmail(), equalTo("daniel@mail.com"));
    }

    @Test
    void update() {
        User user = User.builder()
                .name("Old Name")
                .email("old@mail.com")
                .build();
        em.persist(user);

        UserDto updateDto = UserDto.builder()
                .name("New Name")
                .email("new@mail.com")
                .build();

        service.update(user.getId(), updateDto);

        User result = em.find(User.class, user.getId());

        assertThat(result.getName(), equalTo("New Name"));
        assertThat(result.getEmail(), equalTo("new@mail.com"));
    }

    @Test
    void getById() {
        User user = User.builder()
                .name("Find Me")
                .email("find@mail.com")
                .build();
        em.persist(user);

        UserDto result = service.getById(user.getId());

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("Find Me"));
    }

    @Test
    void findAll() {
        User user1 = User.builder().name("Daniel1").email("D1@mail.com").build();
        User user2 = User.builder().name("Daniel2").email("D2@mail.com").build();
        em.persist(user1);
        em.persist(user2);

        List<UserDto> result = service.findAll();

        assertThat(result, hasSize(greaterThanOrEqualTo(2)));
        assertThat(result, hasItem(hasProperty("name", equalTo("Daniel1"))));
        assertThat(result, hasItem(hasProperty("name", equalTo("Daniel2"))));
    }

    @Test
    void delete() {
        User user = User.builder()
                .name("To Delete")
                .email("delete@mail.com")
                .build();
        em.persist(user);

        service.delete(user.getId());

        List<User> result = em.createQuery("Select u from User u where u.id = :id", User.class)
                .setParameter("id", user.getId())
                .getResultList();

        assertThat(result, is(empty()));
    }
}
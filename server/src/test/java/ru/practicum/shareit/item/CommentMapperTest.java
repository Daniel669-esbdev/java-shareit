package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toCommentDto() {
        User author = User.builder().name("Автор").build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Отлично!")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getText()).isEqualTo("Отлично!");
        assertThat(dto.getAuthorName()).isEqualTo("Автор");
    }
}
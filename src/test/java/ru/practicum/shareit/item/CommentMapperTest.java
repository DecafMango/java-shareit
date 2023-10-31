package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DisplayName("Comment mapper test")
public class CommentMapperTest {

    private final User user = new User(1L, "name", "email@email");
    private final Item item = new Item(1L, "name", "description", false, null, user);

    @Test
    @DisplayName("should map Comment to CommentDto")
    public void should_map_Comment_to_CommentDto() {
        Comment comment = new Comment(1L, "text", user, item, LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        assertThat(commentDto.getId(), is(comment.getId()));
        assertThat(commentDto.getText(), is(comment.getText()));
        assertThat(commentDto.getAuthorName(), is(comment.getAuthor().getName()));
        assertThat(commentDto.getCreated(), is(comment.getCreated()));
    }

    @Test
    @DisplayName("should map CommentDto to Comment")
    public void should_map_CommentDto_to_Comment() {
        CommentDto commentDto = new CommentDto(1L, "text", "name", LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, item, user);

        assertThat(comment.getId(), is(commentDto.getId()));
        assertThat(comment.getText(), is(commentDto.getText()));
        assertThat(comment.getCreated(), is(commentDto.getCreated()));
        assertThat(comment.getAuthor().getName(), is(commentDto.getAuthorName()));
    }
}

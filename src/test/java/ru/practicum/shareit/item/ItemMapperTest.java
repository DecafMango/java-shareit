package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Item mapper test")
public class ItemMapperTest {

    @Test
    @DisplayName("should map Item to ItemDto without bookings")
    public void  should_map_Item_to_ItemDto_without_bookings() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        List<Comment> comments = Collections.emptyList();

        ItemDto itemDto = ItemMapper.toItemWithoutBookingsDto(item, comments);

        assertThat(itemDto.getId(), is(item.getId()));
        assertThat(itemDto.getName(), is(item.getName()));
        assertThat(itemDto.getDescription(), is(item.getDescription()));
        assertThat(itemDto.getAvailable(), is(item.getAvailable()));
        assertThat(itemDto.getRequestId(), is(item.getRequestId()));
        assertThat(itemDto.getComments(), is(comments));
    }

    @Test
    @DisplayName("should map Item to ItemDto with bookings")
    public void should_map_Item_to_ItemDto_with_bookings() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        List<Comment> comments = Collections.emptyList();

        ItemDto itemDto = ItemMapper.toItemWithBookingsDto(item, comments, null, null);

        assertThat(itemDto.getId(), is(item.getId()));
        assertThat(itemDto.getName(), is(item.getName()));
        assertThat(itemDto.getDescription(), is(item.getDescription()));
        assertThat(itemDto.getAvailable(), is(item.getAvailable()));
        assertThat(itemDto.getRequestId(), is(item.getRequestId()));
        assertThat(itemDto.getComments(), is(comments));
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    @DisplayName("should map Item to ItemForRequest")
    public void should_map_item_to_ItemForRequest() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        List<Comment> comments = Collections.emptyList();

        ItemDto itemDto = ItemMapper.toItemWithBookingsDto(item, comments, null, null);

        assertThat(itemDto.getId(), is(item.getId()));
        assertThat(itemDto.getName(), is(item.getName()));
        assertThat(itemDto.getDescription(), is(item.getDescription()));
        assertThat(itemDto.getAvailable(), is(item.getAvailable()));
        assertThat(itemDto.getRequestId(), is(item.getRequestId()));
        assertThat(itemDto.getComments(), is(comments));
    }

    @Test
    @DisplayName("should map Item to ItemDtoForRequest")
    public void should_map_Item_to_ItemDtoForRequest() {
        Item item = new Item(1L, "name", "description", true, 1L, null);
        ItemDtoForRequest itemDtoForRequest = ItemMapper.toItemDtoForRequest(item);

        assertThat(itemDtoForRequest.getId(), is(item.getId()));
        assertThat(itemDtoForRequest.getName(), is(item.getName()));
        assertThat(itemDtoForRequest.getDescription(), is(item.getDescription()));
        assertThat(itemDtoForRequest.getRequestId(), is(item.getRequestId()));
        assertThat(itemDtoForRequest.getAvailable(), is(item.getAvailable()));
    }

    @Test
    @DisplayName("should map ItemDto to Item")
    public void should_map_ItemDto_to_Item() {
        ItemDto itemDto = new ItemDto(1L, "name", "description", true, null, null, null, null);
        Item item = ItemMapper.toItem(itemDto, null, new User(1L, "name", "email@email"));

        assertThat(item.getId(), is(itemDto.getId()));
        assertThat(item.getName(), is(itemDto.getName()));
        assertThat(item.getDescription(), is(itemDto.getDescription()));
    }
}

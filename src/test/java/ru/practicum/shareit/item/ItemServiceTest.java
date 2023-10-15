package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotRentedItemException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Item service test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceTest {

    private final EntityManager em;

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    @Order(1)
    @DisplayName("should return user items")
    public void should_return_user_items() {
        List<ItemDto> itemsDto = itemService.getUserItems(1L, 0, 2);

        assertThat(itemsDto.get(0).getId(), is(1L));
        assertThat(itemsDto.get(0).getName(), is("name1"));
        assertThat(itemsDto.get(0).getDescription(), is("description1"));
        assertThat(itemsDto.get(0).getAvailable(), is(true));

        assertThat(itemsDto.get(1).getId(), is(2L));
        assertThat(itemsDto.get(1).getName(), is("name2"));
        assertThat(itemsDto.get(1).getDescription(), is("description2"));
        assertThat(itemsDto.get(1).getAvailable(), is(false));
    }

    @Test
    @Order(2)
    @DisplayName("should return empty item list")
    public void should_return_empty_item_list() {
        List<ItemDto> itemsDto = itemService.getUserItems(2L, 0, 10);

        assertThat(itemsDto.size(), is(0));
    }

    @Test
    @Order(3)
    @DisplayName("should throw exception due to non-existent user")
    public void should_throw_exception_due_to_non_existent_user_1() {
        assertThrows(ObjectNotFoundException.class, () -> itemService.getUserItems(-1L, 0, 10));
    }

    @Test
    @Order(4)
    @DisplayName("should return item by id")
    public void should_return_item_by_id() {
        ItemDto itemDto = itemService.getItem(1L, 1L);

        assertThat(itemDto.getName(), is("name1"));
        assertThat(itemDto.getDescription(), is("description1"));
    }

    @Test
    @Order(5)
    @DisplayName("should throw exception due to non-existent id")
    public void should_throw_exception_due_to_non_existent_id() {
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(-1L, 1L));
    }

    @Test
    @Order(6)
    @DisplayName("should throw exception due to non-existent user")
    public void should_throw_exception_due_to_non_existent_user_2() {
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(1L, -1L));
    }

    @Test
    @Order(7)
    @DisplayName("should not create item due to non-existent user")
    public void should_not_create_item_due_to_non_existent_user() {
        ItemDto itemDto = new ItemDto(null, "name3", "description3", true, null,
                null, null, null);
        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(itemDto, -1L));
    }

    @Test
    @Order(8)
    @DisplayName("should not create comment due to user did not rent it")
    public void should_not_create_comment_due_to_user_did_not_rent_id() {
        CommentDto commentDto = new CommentDto(null, "text", null, null);

        assertThrows(UserHaveNotRentedItemException.class,
                () -> itemService.createComment(commentDto, 1L, 3L));
    }

    @Test
    @Order(9)
    @DisplayName("should update item")
    public void should_update_item() {
        ItemDto itemDto = new ItemDto(1L, "replaced name", null,
                null, null, null, null, null);
        itemService.updateItem(itemDto, 1L);
        TypedQuery<Item> query = em.createQuery("select i from Item as i where id=:id", Item.class);
        Item item = query.setParameter("id", 1L).getSingleResult();

        assertThat(item.getName(), is("replaced name"));
    }

    @Test
    @Order(10)
    @DisplayName("should not update item due to non-existent user")
    public void should_not_update_item_due_to_non_existent_user() {
        ItemDto itemDto = new ItemDto(1L, "replaced name", null,
                null, null, null, null, null);
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemDto, -1L));
    }

    @Test
    @Order(11)
    @DisplayName("should not update non-existent item")
    public void should_not_update_non_existent_item() {
        ItemDto itemDto = new ItemDto(-1L, "replaced name", null,
                null, null, null, null, null);
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemDto, 1L));
    }

    @Test
    @Order(12)
    @DisplayName("should search items by text")
    public void should_search_items_by_text() {
        List<ItemDto> itemsDto = itemService.searchItems("name", 0, 2);

        assertThat(itemsDto.size(), is(1));
        assertThat(itemsDto.get(0).getName(), is("name1"));
        assertThat(itemsDto.get(0).getDescription(), is("description1"));
    }

    @Test
    @Order(13)
    @DisplayName("should not update item due to not access")
    public void should_not_update_item_due_to_not_access() {
        ItemDto itemDto = new ItemDto(1L, "replaced name", null,
                null, null, null, null, null);
        assertThrows(NoAccessException.class, () -> itemService.updateItem(itemDto, 3L));
    }
}


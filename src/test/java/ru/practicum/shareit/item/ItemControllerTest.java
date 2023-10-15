package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotRentedItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@DisplayName("Item controller test")
public class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final ItemDto itemDto = new ItemDto(1L, "name", "description",
            true, 1L, Collections.emptyList(), null, null);
    private final CommentDto commentDto = new CommentDto(1L, "text", "name", LocalDateTime.now());

    @Test
    @DisplayName("should return all user items")
    public void should_return_all_user_items() throws Exception {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto, itemDto));

        List<ItemDto> expectedResult = List.of(itemDto, itemDto);

        mockMvc.perform(get("/items?from=0&size=2")
                        .header(HeaderNames.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedResult)));

        verify(itemService, times(1))
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should return user items by id=1")
    public void should_return_user_by_id_1() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header(HeaderNames.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(1)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())));

        verify(itemService, times(1))
                .getItem(anyLong(), anyLong());
    }

    @Test
    @DisplayName("should return items for search")
    public void should_return_items_for_search() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .param("text", "123")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(1)))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking())));

        verify(itemService, times(1))
                .searchItems(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should create item")
    public void should_create_user() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(1)))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())));

        verify(itemService, times(1))
                .createItem(any(), anyLong());
    }

    @Test
    @DisplayName("should not create item due to non-existent user")
    public void should_not_create_item_due_to_non_existent_user() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenThrow(new ObjectNotFoundException(""));

        mockMvc.perform(post("/items")
                        .header(HeaderNames.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should update item")
    public void should_update_item() throws Exception {
        when(itemService.updateItem(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderNames.USER_ID_HEADER, 1L))
                .andExpect(status().isOk());

        verify(userService, never())
                .createUser(any());
    }

    @Test
    @DisplayName("should not update item due to non-existent id")
    public void should_not_update_item_due_to_non_existent_id() throws Exception {
        when(itemService.updateItem(any(), anyLong()))
                .thenThrow(new ObjectNotFoundException(""));

        mockMvc.perform(patch("/items/-1")
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderNames.USER_ID_HEADER, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should not create comment due to user did not rent it")
    public void should_not_create_comment_due_to_user_did_not_rent_id() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenThrow(UserHaveNotRentedItemException.class);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HeaderNames.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());
    }
}

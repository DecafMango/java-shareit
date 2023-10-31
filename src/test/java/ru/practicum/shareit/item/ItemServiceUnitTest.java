package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Item service unit test")
public class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("should create item")
    public void should_create_item() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User(1L, "name", "email")));
        when(itemRepository.save(any()))
                .thenReturn(new Item(1L, "name", "description", true, null, new User(1L, "name", "email")));

        ItemDto itemDto = new ItemDto(null, "name", "description", true, null, null, null, null);

        ItemDto createdItemDto = itemService.createItem(itemDto, 1L);

        assertThat(createdItemDto.getId(), is(1L));
        assertThat(createdItemDto.getName(), is("name"));
        assertThat(createdItemDto.getDescription(), is("description"));
    }

}

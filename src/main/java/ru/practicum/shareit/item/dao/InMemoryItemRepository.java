package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final UserRepository userRepository;

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> userItems = new HashMap<>(); // Таблица, содержащая id вещей пользователя
    private Long idCounter = 0L;

    @Override
    public List<ItemDto> getItems() {
        return items.keySet().stream()
                .map(id -> ItemMapper.toItemDto(items.get(id), id))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userRepository.getUser(userId);
        List<Long> itemsId = userItems.get(userId);
        if (itemsId == null)
            return new ArrayList<>();
        return itemsId.stream()
                .map(id -> ItemMapper.toItemDto(items.get(id), id))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null)
            throw new ObjectNotFoundException("Вещь с id=" + itemId + " не найдена");
        return ItemMapper.toItemDto(item, itemId);
    }

    @Override
    public ItemDto createItem(Item item, Long ownerId) {
        userRepository.getUser(ownerId);
        idCounter++;
        items.put(idCounter, item);
        if (userItems.get(ownerId) == null) {
            List<Long> ownerItems = new ArrayList<>();
            ownerItems.add(idCounter);
            userItems.put(ownerId, ownerItems);
        } else {
            userItems.get(ownerId).add(idCounter);
        }
        return ItemMapper.toItemDto(item, idCounter);
    }

    @Override
    public ItemDto updateItem(Item updatedItem, Long itemId, Long redactorId) {
        getItem(itemId);
        userRepository.getUser(redactorId);
        if (userItems.get(redactorId) == null || !userItems.get(redactorId).contains(itemId))
            throw new NoAccessException("Пользователь с id=" + redactorId + " не является владельцем вещи с id=" + itemId);
        Item item = ItemMapper.toItem(getItem(itemId));
        if (updatedItem.getName() != null)
            item.setName(updatedItem.getName());
        if (updatedItem.getDescription() != null)
            item.setDescription(updatedItem.getDescription());
        if (updatedItem.getAvailable() != null)
            item.setAvailable(updatedItem.getAvailable());
        items.put(itemId, item);
        return ItemMapper.toItemDto(item, itemId);
    }

    // Согласно тестам, эта фича пока не предусмотрена
//    @Override
//    public void deleteItem(Long itemId) {
//
//    }
}

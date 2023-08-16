package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<ItemDto> getItems();

    List<ItemDto> getUserItems(Long userId);

    ItemDto getItem(Long itemId);

    ItemDto createItem(Item item, Long ownerId);

    ItemDto updateItem(Item updatedItem, Long itemId, Long redactorId);
    //void deleteItem(Long itemId); // Пока по тестам эта фича не предусмотрена
}

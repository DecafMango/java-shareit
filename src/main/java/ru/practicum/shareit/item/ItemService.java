package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.getUserItems(userId);
    }

    public ItemDto getItem(Long itemId) {
        return itemRepository.getItem(itemId);
    }

    public ItemDto createItem(Item item, Long ownerId) {
        ItemValidator.checkAllFields(item);
        return itemRepository.createItem(item, ownerId);
    }

    public ItemDto updateItem(Item updatedItem, Long itemId, Long redactorId) {
        ItemValidator.checkNotNullFields(updatedItem);
        return itemRepository.updateItem(updatedItem, itemId, redactorId);
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank())
            return new ArrayList<>();
        return itemRepository.getItems().stream()
                .filter(itemDto ->
                        (itemDto.getAvailable() &&
                                (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                                        itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                        )) // Отбираем доступные вещи, подходящие по тексту
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> getUserItems(Long userId) {
        checkUser(userId);
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItem(Long itemId) {
        Item item = checkItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = checkUser(ownerId);
        ItemValidator.checkAllFields(itemDto);
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner)));
    }

    public ItemDto updateItem(ItemDto updatedItemDto, Long redactorId) {
        Item item = checkItem(updatedItemDto.getId());
        checkUser(redactorId);
        if (!item.getOwner().getId().equals(redactorId))
            throw new NoAccessException("Пользователь с id=" + redactorId + " не является владельцем веши с id="
                    + item.getId());
        ItemValidator.checkNotNullFields(updatedItemDto);
        if (updatedItemDto.getName() != null)
            item.setName(updatedItemDto.getName());
        if (updatedItemDto.getDescription() != null)
            item.setDescription(updatedItemDto.getDescription());
        if (updatedItemDto.getAvailable() != null)
            item.setAvailable(updatedItemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if  (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            throw new ObjectNotFoundException("Вещи с id=" + itemId + " не существует");
        return itemOptional.get();
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Начало обработки запроса на получение всех вещей пользователя с id={}", userId);
        List<ItemDto> itemsDto = itemService.getUserItems(userId);
        log.info("Завершение обработки запроса на получение всех вещей пользователя с id={}", userId);
        return itemsDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Positive Long itemId) {
        log.info("Начало обработки запроса на получение вещи с id={}", itemId);
        ItemDto itemDto = itemService.getItem(itemId);
        log.info("Завершение обработки запроса на получение вещи с id={}", itemId);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Начало обработки запроса на поиск вещей по запросу: {}", text);
        List<ItemDto> itemsDto = itemService.searchItems(text);
        log.info("Завершение обработки запроса на поиск вещей по запросу: {}", text);
        return itemsDto;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId) {
        log.info("Начало обработки запроса на создание вещи: {} пользователем с id={}", item, ownerId);
        ItemDto itemDto = itemService.createItem(item, ownerId);
        log.info("Завершение обработки запроса на создание вещи: {} пользователем с id={}", item, ownerId);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody Item updatedItem, @PathVariable @Positive Long itemId, @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Начало обработки запроса на обновление вещи с id={} пользователем с id={}: {}", itemId, userId, updatedItem);
        ItemDto itemDto = itemService.updateItem(updatedItem, itemId, userId);
        log.info("Завершение обработки запроса на обновление вещи с id={} пользователем с id={}: {}", itemId, userId, updatedItem);
        return itemDto;
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId, @PathVariable @Positive Long itemId) {
        log.info("Начало обработки запроса на получение вещи с id={} от пользователя с id={}", itemId, userId);
        ItemDto itemDto = itemService.getItem(itemId, userId);
        log.info("Окончание обработки запроса на получение вещи с id={} от пользователя с id={}", itemId, userId);
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
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId) {
        log.info("Начало обработки запроса на создание вещи: {} пользователем с id={}", itemDto, ownerId);
        ItemDto createdItemDto = itemService.createItem(itemDto, ownerId);
        log.info("Завершение обработки запроса на создание вещи: {} пользователем с id={}", itemDto, ownerId);
        return createdItemDto;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @Positive @PathVariable Long itemId,
                                    @Positive @RequestHeader("X-Sharer-User-Id") Long commentatorId) {
        log.info("Начало обработки запроса на создание комментария: {} пользователем с id={}", commentDto, commentatorId);
        CommentDto createdCommentDto = itemService.createComment(commentDto, itemId, commentatorId);
        log.info("Окончание обработки запроса на создание комментария: {} пользователем с id={}", createdCommentDto, commentatorId);
        return createdCommentDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto updatedItemDto, @PathVariable @Positive Long itemId,
                              @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        updatedItemDto.setId(itemId);
        log.info("Начало обработки запроса на обновление вещи пользователем с id={}: {}", userId, updatedItemDto);
        ItemDto itemDto = itemService.updateItem(updatedItemDto, userId);
        log.info("Завершение обработки запроса на обновление вещи пользователем с id={}: {}", userId, updatedItemDto);
        return itemDto;
    }
}

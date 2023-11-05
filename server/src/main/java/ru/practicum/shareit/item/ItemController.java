package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getUserItems(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items for user with id={}, from={}, size={}", userId, from, size);
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId, @Positive @PathVariable long itemId) {
        log.info("Get item with id={} for user with id={}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Searching items for text: {}, from={}, size={}", text, from, size);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping
    public ItemDto createItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long ownerId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Creating item {} from user with id={}", itemDto, ownerId);
        return itemService.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long commentatorId,
                                                @Positive @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {} from user with id={}", commentDto, commentatorId);
        return itemService.createComment(commentatorId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        log.info("Updating item with id={} following parameters {} from user with id={}", itemId, userId, itemDto);
        return itemService.updateItem(userId, itemDto);
    }
}

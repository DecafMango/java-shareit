package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items for user with id={}, from={}, size={}", userId, from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId, @Positive @PathVariable long itemId) {
        log.info("Get item with id={} for user with id={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "size", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Searching items for text: {}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long ownerId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Creating item {} from user with id={}", itemDto, ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long commentatorId,
                                                @Positive @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {} from user with id={}", commentDto, commentatorId);
        return itemClient.createComment(commentatorId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item with id={} following parameters {} from user with id={}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }
}

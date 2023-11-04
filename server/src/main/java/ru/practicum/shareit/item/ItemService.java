package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotRentedItemException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<ItemDto> getUserItems(Long userId, Integer from, Integer size) {
        checkUser(userId);
        Pageable page = null;
        if (from != null && size != null) {
            if (from < 0 || size <= 0)
                throw new ValidationException("Параметры from и size должны быть следующего вида: from >= 0 size > 0");
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            page = PageRequest.of(from / size, size, sort);
        }

        Page<Item> items = itemRepository.findAllByOwner_IdOrderById(userId, page);
        List<Long> itemIds = items.getContent()
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemIds);
        return items.getContent()
                .stream()
                .map(item -> ItemMapper.toItemWithBookingsDto(item, comments
                                .stream()
                                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                                .collect(Collectors.toList()),
                        findLastBooking(item.getId()),
                        findNextBooking(item.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemDto getItem(Long userId, Long itemId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        if (item.getOwner().getId().equals(userId))
            return ItemMapper.toItemWithBookingsDto(item, comments, findLastBooking(itemId), findNextBooking(itemId));
        else
            return ItemMapper.toItemWithoutBookingsDto(item, comments);
    }

    @Transactional
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        User owner = checkUser(ownerId);
        Request request = checkRequest(itemDto.getRequestId());
        Item createdItem = itemRepository.save(ItemMapper.toItem(itemDto, request, owner));
        if (request != null) {
            request.getItems().add(createdItem);
            requestRepository.save(request);
        }
        return ItemMapper.toItemWithoutBookingsDto(createdItem, Collections.emptyList());
    }

    @Transactional
    public CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = checkUser(authorId);
        Item item = checkItem(itemId);
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, item, author);
        if (bookingRepository.findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(authorId, itemId, LocalDateTime.now())
                .isEmpty())
            throw new UserHaveNotRentedItemException("Пользователь с id=" + authorId + " не может комментировать вещь с id=" +
                    itemId + ", т.к. не арендовал ее ранее");
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public ItemDto updateItem(Long redactorId, ItemDto updatedItemDto) {
        Item item = checkItem(updatedItemDto.getId());
        checkUser(redactorId);
        if (!item.getOwner().getId().equals(redactorId))
            throw new NoAccessException("Пользователь с id=" + redactorId + " не является владельцем веши с id="
                    + item.getId());
        if (updatedItemDto.getName() != null)
            item.setName(updatedItemDto.getName());
        if (updatedItemDto.getDescription() != null)
            item.setDescription(updatedItemDto.getDescription());
        if (updatedItemDto.getAvailable() != null)
            item.setAvailable(updatedItemDto.getAvailable());

        return ItemMapper.toItemWithBookingsDto(itemRepository.save(item), commentRepository.findAllByItem_Id(item.getId()),
                findLastBooking(item.getId()), findNextBooking(item.getId()));

    }

    @Transactional
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        Pageable page = null;
        if (from != null && size != null) {
            if (from < 0 || size <= 0)
                throw new ValidationException("Параметры from и size должны быть следующего вида: from >= 0 size > 0");
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            page = PageRequest.of(from / size, size, sort);
        }
        if (text == null || text.isBlank())
            return Collections.emptyList();
        List<Item> items = null;
        if (page == null)
            items = itemRepository.findAll();
        else
            items = itemRepository.findAll(page).getContent();
        List<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItem_IdIn(itemIds);
        return items
                .stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase())
                                || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(item -> ItemMapper.toItemWithoutBookingsDto(item, comments
                        .stream()
                        .filter(comment -> comment.getItem().getId().equals(item.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            throw new ObjectNotFoundException("Вещи с id=" + itemId + " не существует");
        return itemOptional.get();
    }

    private Request checkRequest(Long requestId) {
        if (requestId == null)
            return null;
        Optional<Request> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty())
            throw new ObjectNotFoundException("Запроса на вещь с id=" + requestId + " не существует");
        return requestOptional.get();
    }

    private Booking findLastBooking(Long itemId) {
        List<Booking> lastApprovedBookings = bookingRepository.findAllByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (lastApprovedBookings.isEmpty())
            return null;
        else
            return lastApprovedBookings.get(0);
    }

    private Booking findNextBooking(Long itemId) {
        List<Booking> futureApprovedBookings = bookingRepository.findAllByItem_IdAndStartAfterAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (futureApprovedBookings.isEmpty())
            return null;
        else
            return futureApprovedBookings.get(futureApprovedBookings.size() - 1);
    }
}

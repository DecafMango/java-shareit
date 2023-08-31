package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {
    public static ItemDto toItemWithoutBookingsDto(Item item, List<Comment> comments) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                comments
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()),
                null,
                null
        );
    }

    public static ItemDto toItemWithBookingsDto(Item item, List<Comment> comments, Booking lastBooking,
                                                            Booking nextBooking) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                comments
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()),
                lastBooking == null ? null : BookingMapper.responseBookingForItemDto(lastBooking),
                nextBooking == null ? null : BookingMapper.responseBookingForItemDto(nextBooking));
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
    }
}

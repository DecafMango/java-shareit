package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {
    public static ItemDto toItemWithoutBookingsDto(Item item, List<Comment> comments) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId(),
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
                item.getRequestId(),
                comments
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()),
                lastBooking == null ? null : BookingMapper.responseBookingForItemDto(lastBooking),
                nextBooking == null ? null : BookingMapper.responseBookingForItemDto(nextBooking));
    }

    public static ItemDtoForRequest toItemDtoForRequest(Item item) {
        return new ItemDtoForRequest(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId());
    }

    public static Item toItem(ItemDto itemDto, Request request,  User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                request == null ? null : request.getId(), owner);
    }
}

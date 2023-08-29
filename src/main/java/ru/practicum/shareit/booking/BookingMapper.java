package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    // RequestBookingDto --> Booking
    public static Booking toBooking(RequestBookingDto requestBookingDto, Item item, User booker, BookingStatus status) {
        return new Booking(null, requestBookingDto.getStart(), requestBookingDto.getEnd(), item, booker, status);
    }

    // Booking --> ResponseBooking
    public static ResponseBookingDto toResponseBookingDto(Booking booking) {
        return new ResponseBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()), UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }
}

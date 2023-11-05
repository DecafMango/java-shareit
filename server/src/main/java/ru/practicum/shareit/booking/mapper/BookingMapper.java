package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

public class BookingMapper {
    // RequestBookingDto --> Booking
    public static Booking toBooking(RequestBookingDto requestBookingDto, Item item, User booker, BookingStatus status) {
        return new Booking(null, requestBookingDto.getStart(), requestBookingDto.getEnd(), item, booker, status);
    }

    // Booking --> ResponseBooking
    public static ResponseBookingDto toResponseBookingDto(Booking booking) {
        return new ResponseBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                ItemMapper.toItemWithoutBookingsDto(booking.getItem(), Collections.emptyList()), UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }

    // Booking --> ResponseBookingForItemDto
    public static ResponseBookingForItemDto responseBookingForItemDto(Booking booking) {
        return new ResponseBookingForItemDto(booking.getId(), booking.getBooker().getId(), booking.getStart(),
                booking.getEnd());
    }


}

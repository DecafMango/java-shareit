package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Booking mapper test")
public class BookingMapperTest {

    private final User user = new User(1L, "name1", "email1@email");
    private final UserDto userDto =  new UserDto(1L, "name1", "email1@email");
    private final Item item = new Item(1L, "name1", "description1", false, null,
            null);
    private final ItemDto itemDto = new ItemDto(1L, "name1", "description1", false,
            null, null, null, null);
    private final RequestBookingDto requestBookingDto = new RequestBookingDto(1L, LocalDateTime.now(),
            LocalDateTime.now());
    private final ResponseBookingDto responseBookingDto = new ResponseBookingDto(1L, LocalDateTime.now(),
            LocalDateTime.now(), itemDto, userDto, BookingStatus.WAITING);
    private final Booking booking = new Booking(1L, LocalDateTime.now(),
            LocalDateTime.now(), item, user, BookingStatus.WAITING);

    @Test
    @DisplayName("should convert RequestBookingDto to Booking")
    public void should_convert_RequestBookingDto_to_Booking() {
        Booking convertedBooking = BookingMapper.toBooking(requestBookingDto, item, user, BookingStatus.WAITING);

        assertThat(convertedBooking.getStart(), is(requestBookingDto.getStart()));
        assertThat(convertedBooking.getEnd(), is(requestBookingDto.getEnd()));
        assertThat(convertedBooking.getItem().getId(), is(requestBookingDto.getItemId()));
    }

    @Test
    @DisplayName("should convert Booking to ResponseBookingDto")
    public void should_convert_Booking_to_ResponseBookingDto() {
        ResponseBookingDto convertedResponseBookingDto = BookingMapper.toResponseBookingDto(booking);

        assertThat(convertedResponseBookingDto.getId(), is(booking.getId()));
        assertThat(convertedResponseBookingDto.getStart(), is(booking.getStart()));
        assertThat(convertedResponseBookingDto.getEnd(), is(booking.getEnd()));
        assertThat(convertedResponseBookingDto.getStatus(), is(booking.getStatus()));
        assertThat(convertedResponseBookingDto.getItem().getName(), is(booking.getItem().getName()));
        assertThat(convertedResponseBookingDto.getItem().getDescription(), is(booking.getItem().getDescription()));
        assertThat(convertedResponseBookingDto.getBooker().getName(), is(booking.getBooker().getName()));
        assertThat(convertedResponseBookingDto.getBooker().getEmail(), is(booking.getBooker().getEmail()));
    }

    @Test
    @DisplayName("should convert Booking to ResponseBookingForItemDto")
    public void should_convert_Booking_to_ResponseBookingForItemDto() {
        ResponseBookingForItemDto convertedResponseBookingForItemDto = BookingMapper.responseBookingForItemDto(booking);

        assertThat(convertedResponseBookingForItemDto.getId(), is(booking.getId()));
        assertThat(convertedResponseBookingForItemDto.getStart(), is(booking.getStart()));
        assertThat(convertedResponseBookingForItemDto.getEnd(), is(booking.getEnd()));
        assertThat(convertedResponseBookingForItemDto.getBookerId(), is(booking.getBooker().getId()));
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Booking service test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    @DisplayName("should return booking by id")
    @Order(1)
    public void should_return_booking_by_id() {
        ResponseBookingDto responseBookingDto = bookingService.getBooking(1L, 2L);

        assertThat(responseBookingDto.getId(), is(1L));
        assertThat(responseBookingDto.getItem().getId(), is(1L));
        assertThat(responseBookingDto.getBooker().getId(), is(2L));
    }

    @Test
    @DisplayName("should throw exception due to non-existent object (user/booking)")
    @Order(2)
    public void should_throw_exception_due_to_non_existent_object() {
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(-1L, 1L));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(1L, -1L));
    }

    @Test
    @DisplayName("should throw exception due to not access")
    @Order(3)
    public void should_throw_exception_due_to_not_access() {
        assertThrows(NoAccessException.class, () -> bookingService.getBooking(1L, 3L));
    }

    @Test
    @DisplayName("should return all user bookings")
    @Order(4)
    public void should_return_all_user_bookings() {
        List<ResponseBookingDto> responseBookingsDto = bookingService.getBookings("ALL", 2L, 0, 1);

        assertThat(responseBookingsDto.get(0).getId(), is(1L));
        assertThat(responseBookingsDto.get(0).getItem().getId(), is(1L));
        assertThat(responseBookingsDto.get(0).getBooker().getId(), is(2L));
    }

    @Test
    @DisplayName("should return empty list of bookings")
    @Order(5)
    public void should_return_empty_list_of_bookings() {
        List<ResponseBookingDto> responseBookingsDto = bookingService.getBookings("ALL", 1L, null, null);

        assertThat(responseBookingsDto.size(), is(0));
    }

    @Test
    @DisplayName("should throw exception due to invalid state")
    @Order(6)
    public void should_throw_exception_due_to_invalid_state_1() {
        assertThrows(ValidationException.class, () -> bookingService.getBookings("aaa", 1L, null, null));
    }

    @Test
    @DisplayName("should return all owner bookings")
    @Order(7)
    public void should_return_all_owner_bookings() {
        List<ResponseBookingDto> responseBookingsDto = bookingService.getOwnerBookings("ALL", 1L, 0, 2);

        assertThat(responseBookingsDto.get(0).getId(), is(1L));
        assertThat(responseBookingsDto.get(0).getItem().getId(), is(1L));
        assertThat(responseBookingsDto.get(0).getBooker().getId(), is(2L));
    }

    @Test
    @DisplayName("should throw exception due to invalid state")
    @Order(8)
    public void should_throw_exception_due_to_invalid_state_2() {
        assertThrows(ValidationException.class, () -> bookingService.getOwnerBookings("aaa", 1L, null, null));
    }

    @Test
    @DisplayName("should approve on booking")
    @Order(9)
    public void should_approve_on_booking() {
        bookingService.answerOnBooking(1L, true, 1L);

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b where id=:id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(booking.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    @DisplayName("should not answer on booking due to not access")
    @Order(10)
    public void should_not_answer_on_booking_due_to_not_access() {
        assertThrows(NoAccessException.class, () -> bookingService.answerOnBooking(1L, true, 3L));
    }

    @Test
    @DisplayName("should throw exception due to invalid from and size parameters")
    @Order(11)
    public void should_throw_exception_due_to_invalid_from_and_size_parameters() {
        assertThrows(ValidationException.class, () -> bookingService.getBookings("ALL", 1L, -1, -1));
        assertThrows(ValidationException.class, () -> bookingService.getOwnerBookings("ALL", 1L, -1, -1));
    }

    @Test
    @DisplayName("should not create booking due to non-existent user")
    @Order(12)
    public void should_not_create_booking_due_to_non_existent_user() {
        RequestBookingDto requestBookingDto = new RequestBookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(requestBookingDto, -1L));
    }

    @Test
    @DisplayName("should return empty lists to these states")
    @Order(13)
    public void should_return_empty_lists_to_these_states() {
        assertThat(bookingService.getBookings("CURRENT", 3L, null, null).size(), is(0));
        assertThat(bookingService.getBookings("PAST", 3L, null, null).size(), is(0));
        assertThat(bookingService.getBookings("FUTURE", 3L, null, null).size(), is(0));
        assertThat(bookingService.getBookings("WAITING", 3L, null, null).size(), is(0));
        assertThat(bookingService.getBookings("REJECTED", 3L, null, null).size(), is(0));assertThat(bookingService.getBookings("CURRENT", 3L, null, null).size(), is(0));
        assertThat(bookingService.getOwnerBookings("CURRENT", 3L, null, null).size(), is(0));
        assertThat(bookingService.getOwnerBookings("PAST", 3L, null, null).size(), is(0));
        assertThat(bookingService.getOwnerBookings("FUTURE", 3L, null, null).size(), is(0));
        assertThat(bookingService.getOwnerBookings("WAITING", 3L, null, null).size(), is(0));
        assertThat(bookingService.getOwnerBookings("REJECTED", 3L, null, null).size(), is(0));
    }

    @Test
    @DisplayName("should throw exception due to booking already answered")
    @Order(14)
    public void should_throw_exception_due_to_booking_already_answered() {
        bookingService.answerOnBooking(1L, true, 1L);

        assertThrows(BookingIsAlreadyAnswered.class, () -> bookingService.answerOnBooking(1L, true, 1L));
    }

    @Test
    @DisplayName("should throw exception due to item is unavailable")
    @Order(15)
    public void should_throw_exception_due_to_item_is_unavailable() {
        RequestBookingDto requestBookingDto = new RequestBookingDto(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(ItemUnavailableException.class, () -> bookingService.createBooking(requestBookingDto, 3L));
    }

    @Test
    @DisplayName("should reject on booking")
    @Order(16)
    public void should_reject_on_booking() {
        bookingService.answerOnBooking(1L, false, 1L);

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b where id=:id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertThat(booking.getStatus(), is(BookingStatus.REJECTED));
    }

}

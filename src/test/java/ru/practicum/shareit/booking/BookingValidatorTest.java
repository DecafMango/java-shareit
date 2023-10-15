package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Booking validator test")
public class BookingValidatorTest {

    @Test
    @DisplayName("should validate booking")
    public void should_validate_booking() {
        RequestBookingDto validRequestBookingDto = new RequestBookingDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(10));
        BookingValidator.checkNecessaryFields(validRequestBookingDto);
    }

    @Test
    @DisplayName("should not validate booking due to invalid id")
    public void should_not_validate_booking_due_to_invalid_id() {
        RequestBookingDto invalidRequestBookingDto = new RequestBookingDto(-1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        assertThrows(ValidationException.class, () -> BookingValidator.checkNecessaryFields(invalidRequestBookingDto));
    }

    @Test
    @DisplayName("should not validate booking due to start in past")
    public void should_not_validate_booking_due_to_start_in_past() {
        RequestBookingDto invalidRequestBookingDto = new RequestBookingDto(1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class, () -> BookingValidator.checkNecessaryFields(invalidRequestBookingDto));
    }

    @Test
    @DisplayName("should not validate booking due to end before start")
    public void should_not_validate_booking_due_to_end_before_start() {
        RequestBookingDto invalidRequestBookingDto = new RequestBookingDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> BookingValidator.checkNecessaryFields(invalidRequestBookingDto));
    }
}

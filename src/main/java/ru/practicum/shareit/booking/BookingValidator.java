package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {

    // метод, проверяющий обязательные поля: id вещи и время старта/окончания аренды
    public static void checkNecessaryFields(RequestBookingDto bookingDto) {
        if (bookingDto.getItemId() == null || bookingDto.getItemId() <= 0) {
            throw new ValidationException("Идентификатор вещи должен быть положительным");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null)
            throw new ValidationException("Время начала и конца аренды вещи не должны быть пустыми");
        if (bookingDto.getStart().equals(bookingDto.getEnd()) ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()))
            throw new ValidationException("Время начала аренды должно быть раньше ее конца");
        if (bookingDto.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Время начала аренды не может быть в прошлом");
    }
}

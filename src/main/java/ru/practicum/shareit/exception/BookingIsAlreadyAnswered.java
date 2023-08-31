package ru.practicum.shareit.exception;

public class BookingIsAlreadyAnswered extends RuntimeException {
    public BookingIsAlreadyAnswered(String message) {
        super(message);
    }
}

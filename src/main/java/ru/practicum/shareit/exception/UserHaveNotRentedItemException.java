package ru.practicum.shareit.exception;

public class UserHaveNotRentedItemException extends RuntimeException {
    public UserHaveNotRentedItemException(String message) {
        super(message);
    }
}

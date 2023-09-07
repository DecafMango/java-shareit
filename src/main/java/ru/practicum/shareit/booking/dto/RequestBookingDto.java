package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

// класс, предназначенный на создание запроса на аренду вещи
@Data
public class RequestBookingDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

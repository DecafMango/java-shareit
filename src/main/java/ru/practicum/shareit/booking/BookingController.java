package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@Positive @PathVariable Long bookingId,
                                         @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId) {
        log.info("Начало обработки запроса на получение запроса на аренду с id={} пользователем с id={}",
                bookingId, userId);
        ResponseBookingDto responseBookingDto = bookingService.getBooking(bookingId, userId);
        log.info("Окончание обработки запроса на получение запроса на аренду с id={} пользователем с id={}",
                bookingId, userId);
        return responseBookingDto;
    }

    @GetMapping
    public List<ResponseBookingDto> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                                @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Начало обработки запроса на получение {} запросов на аренду пользователя с id={}", state, userId);
        List<ResponseBookingDto> responseBookingDtos = bookingService.getBookings(state, userId, from, size);
        log.info("Окончание обработки запроса на получение {} запросов на аренду пользователя с id={}", state, userId);
        return responseBookingDtos;
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                     @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size) {
        log.info("Начало обработки запроса на получение {} запросов на аренду вещей пользователя с id={}", state,
                userId);
        List<ResponseBookingDto> responseOwnerBookingDtos = bookingService.getOwnerBookings(state, userId, from, size);
        log.info("Начало обработки запроса на получение {} запросов на аренду вещей пользователя с id={}", state,
                userId);
        return responseOwnerBookingDtos;
    }

    @PostMapping
    public ResponseBookingDto createBooking(@RequestBody RequestBookingDto requestBookingDto,
                                            @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long bookerId) {
        log.info("Начало обработки запроса на создание запроса на бронирование: {} пользователем с id={}", requestBookingDto,
                bookerId);
        ResponseBookingDto responseBookingDto = bookingService.createBooking(requestBookingDto, bookerId);
        log.info("Окончание обработки запроса на создание запроса на бронирование: {} пользователем с id={}",
                responseBookingDto,
                bookerId);
        return responseBookingDto;
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto responseOnBooking(@Positive @PathVariable Long bookingId, @RequestParam Boolean approved,
                                                @Positive @RequestHeader(HeaderNames.USER_ID_HEADER) Long userId) {
        log.info("Начало обработки запроса на ответ approved={} на запрос аренды с id={} от пользователя с id={}",
                approved, bookingId, userId);
        ResponseBookingDto responseBookingDto = bookingService.answerOnBooking(bookingId, approved, userId);
        log.info("Окончание обработки запроса на ответ approved={} на запрос аренды с id={} от пользователя с id={}",
                approved, bookingId, userId);
        return responseBookingDto;
    }
}

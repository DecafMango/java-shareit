package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                             @Positive @PathVariable long bookingId) {
        log.info("Getting booking with id={} for user with id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        log.info("Get user with id={} bookings with state={}", userId, state);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        log.info("Getting owner with id={} bookings with state={}", userId, state);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long bookerId,
                                                @RequestBody BookingDto bookingDto) {
        log.info("Creating booking {} from user with id={}", bookingDto, bookerId);
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> responseOnBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                                    @Positive @PathVariable long bookingId,
                                                    @RequestParam boolean approved) {
        log.info("Giving response approved={} to booking with id={} from user with id={}", approved, bookingId, userId);
        return bookingClient.responseOnBooking(userId, bookingId, approved);
    }
}

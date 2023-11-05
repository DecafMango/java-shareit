package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderNames;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                         @Positive @PathVariable long bookingId) {
        log.info("Getting booking with id={} for user with id={}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<ResponseBookingDto> getBookings(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                                @RequestParam(name = "state", defaultValue = "all") String state,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get user with id={} bookings with state={}", userId, state);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getOwnerBookings(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String state,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting owner with id={} bookings with state={}", userId, state);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseBookingDto createBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long bookerId,
                                                @RequestBody RequestBookingDto bookingDto) {
        log.info("Creating booking {} from user with id={}", bookingDto, bookerId);
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto responseOnBooking(@Positive @RequestHeader(HeaderNames.USER_ID_HEADER) long userId,
                                                    @Positive @PathVariable long bookingId,
                                                    @RequestParam boolean approved) {
        log.info("Giving response approved={} to booking with id={} from user with id={}", approved, bookingId, userId);
        return bookingService.answerOnBooking(userId, bookingId, approved);
    }
}

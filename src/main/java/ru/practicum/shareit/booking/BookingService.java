package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ResponseBookingDto getBooking(Long bookingId, Long userId) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)))
                throw new NoAccessException("Пользователь с id=" + userId + " не является ни создателем запроса на " +
                        "аренду с id=" + bookingId + ", ни владельцем арендуемой вещи с id=" +
                        booking.getItem().getId());
        return BookingMapper.toResponseBookingDto(booking);
    }

    public List<ResponseBookingDto> getBookings(String state, Long userId) {
        checkUser(userId);
        state = state.toUpperCase();
        List<Booking> queryResult = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                queryResult = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                queryResult = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId)
                        .stream()
                        .filter(booking -> !(currentTime.isBefore(booking.getStart()) ||
                                currentTime.isAfter(booking.getEnd())))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                queryResult = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId)
                        .stream()
                        .filter(booking -> currentTime.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                queryResult = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId)
                        .stream()
                        .filter(booking -> currentTime.isBefore(booking.getStart()))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                queryResult = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case "REJECTED":
                queryResult = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Не существует значения параметра state=" + state);
        }
        return queryResult
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    public List<ResponseBookingDto> getOwnerBookings(String state, Long ownerId) {
        checkUser(ownerId);
        state = state.toUpperCase();
        List<Booking> queryResult = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                queryResult = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                queryResult = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId)
                        .stream()
                        .filter(booking -> !(currentTime.isBefore(booking.getStart()) ||
                                currentTime.isAfter(booking.getEnd())))
                        .collect(Collectors.toList());
                break;
            case "PAST":
                queryResult = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId)
                        .stream()
                        .filter(booking -> currentTime.isAfter(booking.getEnd()))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                queryResult = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(ownerId)
                        .stream()
                        .filter(booking -> currentTime.isBefore(booking.getStart()))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                queryResult = bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING);
                break;
            case "REJECTED":
                queryResult = bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Не существует значения параметра state=" + state);
        }
        return queryResult
                .stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    public ResponseBookingDto createBooking(RequestBookingDto requestBookingDto, Long bookerId) {
        BookingValidator.checkNecessaryFields(requestBookingDto);
        User booker = checkUser(bookerId);
        Item item = checkItem(requestBookingDto.getItemId());
        return BookingMapper.toResponseBookingDto(
                bookingRepository.save(BookingMapper.toBooking(requestBookingDto, item,
                        booker, BookingStatus.WAITING))
        );
    }

    public ResponseBookingDto answerOnBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = checkBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NoAccessException("Пользователь с id=" + userId + " не является владельцем вещи с id=" +
                    booking.getItem().getId());
        }
        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    private User checkUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if  (userOptional.isEmpty())
            throw new ObjectNotFoundException("Пользователя с id=" + userId + " не существует");
        return userOptional.get();
    }

    private Item checkItem(Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty())
            throw new ObjectNotFoundException("Вещи с id=" + itemId + " не существует");
        return itemOptional.get();
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty())
            throw new ObjectNotFoundException("Запроса на аренду с id=" + bookingId + " не существует");
        return bookingOptional.get();
    }
}
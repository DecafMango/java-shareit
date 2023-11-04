package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable page);

    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable page);

    Page<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long bookerId, Long itemId, LocalDateTime currentTime);

    List<Booking> findAllByItem_IdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    List<Booking> findAllByItem_IdAndStartAfterAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);
}

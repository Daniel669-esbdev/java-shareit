package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdInAndStatus(List<Long> itemIds, BookingStatus status, Sort sort);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime end, BookingStatus status);
}
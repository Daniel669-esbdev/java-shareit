package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Booking create(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать свою же вещь");
        }
        validateDates(bookingDto);

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        log.info("Создано бронирование ID {} пользователем ID {}", booking.getId(), userId);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Статус бронирования ID {} изменен на {}", bookingId, booking.getStatus());
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException("Доступ к бронированию разрешен только автору или владельцу вещи");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllByBooker(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        BookingState bookingState = parseState(state);

        switch (bookingState) {
            case CURRENT: return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
            case PAST: return bookingRepository.findAllByBookerIdAndEndBefore(userId, now, sort);
            case FUTURE: return bookingRepository.findAllByBookerIdAndStartAfter(userId, now, sort);
            case WAITING: return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED: return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default: return bookingRepository.findAllByBookerId(userId, sort);
        }
    }

    @Override
    public List<Booking> getAllByOwner(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        BookingState bookingState = parseState(state);

        switch (bookingState) {
            case CURRENT: return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, sort);
            case PAST: return bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, sort);
            case FUTURE: return bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, sort);
            case WAITING: return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED: return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default: return bookingRepository.findAllByItemOwnerId(userId, sort);
        }
    }

    private void validateDates(BookingDto dto) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new ValidationException("Даты бронирования не могут быть пустыми");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть в прошлом");
        }
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().isEqual(dto.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
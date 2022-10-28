package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBooking(long bookingId, long userId) {

        Booking booking = bookingRepository.getBookingById(bookingId);

        if (booking == null) throw new NotFoundException("bookingId: " + bookingId);

        // Может быть выполнено либо автором бронирования, либо владельцем вещи
        if (booking.getBooker().getId() != userId && itemRepository.getItemById(booking.getItem().getId()).getOwnerId() != userId)
            throw new NotFoundException("any booking for userId: " + userId);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookings(String status, long bookerId, int from, int size) {

        if (userRepository.getUserById(bookerId) == null) throw new NotFoundException("Not found user.");
        BookingStatus bookingStatus = bookingStatusValidation(status);

        List<Booking> allBookings = List.of();
        Pageable pageable = PageRequest.of(from / size, size);

        switch (bookingStatus.toString()) {
            case "CURRENT":
                allBookings = bookingRepository.getCurrentBookings(bookerId, pageable);
                break;
            case "PAST":
                allBookings = bookingRepository.getPastBookings(bookerId, pageable);
                break;
            case "FUTURE":
                allBookings = bookingRepository.getFutureBookings(bookerId, pageable);
                break;
            case "WAITING":
                allBookings = bookingRepository.getWaitingBookings(bookerId, pageable);
                break;
            case "REJECTED":
                allBookings = bookingRepository.getRejectedBookings(bookerId, pageable);
                break;
            case "ALL":
                allBookings = bookingRepository.getBookingsByBooker_IdOrderByStartDesc(bookerId, pageable);
                break;
        }

        return getBookingDtos(allBookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getOwnerBookings(String status, long bookerId, int from, int size) {

        List<Booking> allBookings;
        List<Long> userItems = itemRepository.getAllUserItemsId(bookerId);

        if (userItems.size() == 0) throw new NotFoundException("No bookings for this owner");

        BookingStatus bookingStatus = bookingStatusValidation(status);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (bookingStatus.toString()) {
            case "CURRENT":
                allBookings = bookingRepository.getCurrentOwnerBookings(userItems, pageable);
                break;
            case "PAST":
                allBookings = bookingRepository.getPastOwnerBookings(userItems, pageable);
                break;
            case "FUTURE":
                allBookings = bookingRepository.getFutureOwnerBookings(userItems, pageable);
                break;
            case "WAITING":
                allBookings = bookingRepository.getWaitingOwnerBookings(userItems, pageable);
                break;
            case "REJECTED":
                allBookings = bookingRepository.getRejectedOwnerBookings(userItems, pageable);
                break;
            case "ALL":
                allBookings = bookingRepository.getBookingsByItem_IdInOrderByStartDesc(userItems, pageable);
                break;
            default:
                allBookings = List.of();
                break;
        }

        return getBookingDtos(allBookings);
    }

    @Override
    @Transactional
    public BookingDto createBooking(long bookerId, Booking booking) {

        Item itemForBooking = itemRepository.getItemById(booking.getItemId());
        User booker = userRepository.getUserById(bookerId);

        if (booker == null) throw new NotFoundException("Not found user.");
        if (itemForBooking == null) throw new NotFoundException("Not found item for booking.");
        if (!itemForBooking.getAvailable()) throw new BadRequestException("Item not available!");
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new BadRequestException("End of booking could not be before start.");
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Start and end of booking could not be in the past.");
        if (bookingRepository.getCrossBookingsForItem(booking.getStart(), booking.getItemId()).size() > 0)
            throw new BadRequestException("Already booked item with id: " + booking.getItemId());
        if (itemForBooking.getOwnerId() == bookerId)
            throw new NotFoundException("User could not book own item:  " + booking.getItemId());

        log.info("Trying to create booking -> {}", booking);
        booking.setBooker(booker);
        booking.setItem(itemForBooking);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto setApprove(boolean approved, long bookingId, long userId) {

        Booking booking = bookingRepository.getBookingById(bookingId);

        if (booking.getBookingApproved().equals("APPROVED"))
            throw new BadRequestException("No any not approved bookings for userId: " + userId);

        // Если пользователь меняющий статус (userId) не владелец вещи, то отказ
        if (itemRepository.getItemById(booking.getItem().getId()).getOwnerId() != userId)
            throw new NotFoundException("any items for userId: " + userId);

        if (Boolean.toString(approved).equalsIgnoreCase("true")) {
            booking.setBookingApproved("APPROVED");
        } else {
            booking.setBookingApproved("REJECTED");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    private BookingStatus bookingStatusValidation(String state) {

        BookingStatus bookingStatus;

        try {
            bookingStatus = BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }

        return bookingStatus;
    }

    private List<BookingDto> getBookingDtos(List<Booking> allBookings) {
        return allBookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}

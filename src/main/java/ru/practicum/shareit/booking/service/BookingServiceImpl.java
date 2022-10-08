package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    @Autowired
    BookingServiceImpl(BookingRepository bookingRepository,
                       UserRepository userRepository,
                       ItemRepository itemRepository,
                       BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.getBookingById(bookingId);

        if (booking == null) throw new NotFoundException("bookingId: " + bookingId);

        // Может быть выполнено либо автором бронирования, либо владельцем вещи
        if (booking.getBookerId() != userId && itemRepository.getItemById(booking.getId()).getOwnerId() != userId)
            throw new NotFoundException("any booking for userId: " + userId);

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(String status, long bookerId) {

        if (userRepository.getUserById(bookerId) == null) throw new NotFoundException("Not found user.");
        BookingStatus bookingStatus = bookingStatusValidation(status);

        List<Booking> allBookings = List.of();

        switch (bookingStatus.toString()) {
            case "CURRENT":
                allBookings = bookingRepository.getCurrentBookings(bookerId);
                break;
            case "PAST":
                allBookings = bookingRepository.getPastBookings(bookerId);
                break;
            case "FUTURE":
                allBookings = bookingRepository.getFutureBookings(bookerId);
                break;
            case "WAITING":
                allBookings = bookingRepository.getWaitingBookings(bookerId);
                break;
            case "REJECTED":
                allBookings = bookingRepository.getRejectedBookings(bookerId);
                break;
            case "ALL":
                allBookings = bookingRepository.getBookingsByBookerIdOrderByStartDesc(bookerId);
                break;
        }

        return allBookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());

    }

    //TODO Найти все брони для вещей пользователя
    @Override
    public List<BookingDto> getOwnerBookings(String status, long bookerId) {

        List<Long> userItems = itemRepository.getAllUserItemsId(bookerId);

        if (userItems.size() == 0) throw new NotFoundException("No bookings for this owner");

        BookingStatus bookingStatus = bookingStatusValidation(status);
        List<Booking> allBookings = List.of();

        switch (bookingStatus.toString()) {
            case "CURRENT":
                allBookings = bookingRepository.getCurrentOwnerBookings(userItems);
                break;
            case "PAST":
                allBookings = bookingRepository.getPastOwnerBookings(userItems);
                break;
            case "FUTURE":
                allBookings = bookingRepository.getFutureOwnerBookings(userItems);
                break;
            case "WAITING":
                allBookings = bookingRepository.getWaitingOwnerBookings(userItems);
                break;
            case "REJECTED":
                allBookings = bookingRepository.getRejectedOwnerBookings(userItems);
                break;
            case "ALL":
                allBookings = bookingRepository.getBookingsByItemIdInOrderByStartDesc(userItems);
                break;
        }

        return allBookings.stream().map(bookingMapper::toBookingDto).collect(Collectors.toList());

    }

    @Override
    public List<Booking> getAllBookingsForItem(long itemId) {
        return bookingRepository.getBookingsByItemId(itemId);
    }


    @Override
    @Transactional
    public Booking createBooking(long bookerId, Booking booking) {

        Item itemForBooking = itemRepository.getItemById(booking.getItemId());

        if (userRepository.getUserById(bookerId) == null) throw new NotFoundException("Not found user.");
        if (itemForBooking == null) throw new NotFoundException("Not found item for booking.");
        if (!itemForBooking.isAvailable()) throw new BadRequestException("Item not available!");
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new BadRequestException("End of booking could not be before start.");
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Start and end of booking could not be in the past.");
        if (bookingRepository.getCrossBookingsForItem(booking.getStart(), booking.getItemId()).size() > 0)
            throw new BadRequestException("Already booked item with id: " + booking.getItemId());
        if (itemForBooking.getOwnerId() == bookerId)
            throw new NotFoundException("User could not book own item:  " + booking.getItemId());

        log.info("Trying to create booking -> {}", booking);

        booking.setBookerId(bookerId);
        return bookingRepository.save(booking);

    }

    @Override
    @Transactional
    public BookingDto setApprove(boolean approved, long bookingId, long userId) {

        Booking booking = bookingRepository.getBookingById(bookingId);

        if (booking.getBookingApproved().equals("APPROVED"))
            throw new BadRequestException("No any not approved bookings for userId: " + userId);

        // Если пользователь меняющий статус (userId) не владелец вещи, то отказ
        if (itemRepository.getItemById(booking.getItemId()).getOwnerId() != userId)
            throw new NotFoundException("any items for userId: " + userId);

        if (Boolean.toString(approved).equalsIgnoreCase("true")) {
            booking.setBookingApproved("APPROVED");
        } else {
            booking.setBookingApproved("REJECTED");
        }

        return bookingMapper.toBookingDto(booking);
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
}

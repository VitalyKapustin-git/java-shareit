package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDto getBooking(long bookingId, long bookerId);

    List<BookingDto> getAllBookings(String state, long bookerId, int from, int size);

    List<BookingDto> getOwnerBookings(String state, long bookerId, int from, int size);

    BookingDto createBooking(long bookerId, Booking booking);

    BookingDto setApprove(boolean approved, long bookingId, long userId);

}

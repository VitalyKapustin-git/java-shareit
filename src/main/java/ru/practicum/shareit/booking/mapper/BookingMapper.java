package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;


public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getBookingApproved());

        return bookingDto;
    }

}

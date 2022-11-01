package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.mappers.UserMapper;


public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getBookingApproved());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));

        return bookingDto;
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {

        BookingItemDto bookingItemDto = new BookingItemDto();

        bookingItemDto.setId(booking.getId());
        bookingItemDto.setStart(booking.getStart());
        bookingItemDto.setEnd(booking.getEnd());
        bookingItemDto.setStatus(booking.getBookingApproved());
        bookingItemDto.setItemId(booking.getItem().getId());
        bookingItemDto.setBookerId(booking.getBooker().getId());

        return bookingItemDto;
    }

}

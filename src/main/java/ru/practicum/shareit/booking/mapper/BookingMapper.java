package ru.practicum.shareit.booking.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.mappers.UserMapper;

@Component
@AllArgsConstructor
public class BookingMapper {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public BookingDto toBookingDto(Booking booking) {

        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(itemRepository.getItemById(booking.getItemId())));
        bookingDto.setBooker(UserMapper.toUserDto(userRepository.getUserById(booking.getBookerId())));
        bookingDto.setStatus(booking.getBookingApproved());

        return bookingDto;
    }

}

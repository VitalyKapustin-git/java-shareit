package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.mappers.UserMapper;

@Component
public class BookingMapper {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(itemRepository.getItemById(booking.getItemId())))
                .booker(UserMapper.toUserDto(userRepository.getUserById(booking.getBookerId())))
                .status(booking.getBookingApproved())
                .build();
    }

}

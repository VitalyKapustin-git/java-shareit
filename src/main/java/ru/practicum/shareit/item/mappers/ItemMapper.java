package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {

        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getOwnerId());
        item.setRequestId(itemDto.getRequestId());


        return item;

    }


    public static ItemDto toItemDto(Item item) {

        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setRequestId(item.getRequestId());

        return itemDto;

    }

    public static ItemWithBookingDto toItemBookingDto(Item item,
                                                      Booking lastBooking,
                                                      Booking nextBooking,
                                                      List<CommentDto> comments) {

        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();

        itemWithBookingDto.setId(item.getId());
        itemWithBookingDto.setName(item.getName());
        itemWithBookingDto.setDescription(item.getDescription());
        itemWithBookingDto.setAvailable(item.getAvailable());
        itemWithBookingDto.setOwnerId(item.getOwnerId());
        itemWithBookingDto.setLastBooking(lastBooking == null ? null : BookingMapper.toBookingItemDto(lastBooking));
        itemWithBookingDto.setNextBooking(nextBooking == null ? null : BookingMapper.toBookingItemDto(nextBooking));
        itemWithBookingDto.setComments(comments);
        itemWithBookingDto.setRequestId(item.getRequestId());

        return itemWithBookingDto;
    }

}

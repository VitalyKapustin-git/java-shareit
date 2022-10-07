package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, long userId);

    ItemDto update(ItemDto itemDto, long itemId, long userId);

    ItemWithBookingDto get(long itemId, long userId);

    List<ItemWithBookingDto> getAll(long userId);

    void remove(long itemId, long userId);

    List<ItemDto> findByText(String text);
}

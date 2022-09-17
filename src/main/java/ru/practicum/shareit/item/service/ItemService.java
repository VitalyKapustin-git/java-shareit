package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(ItemDto item, long userId);
    Item update(ItemDto itemDto, long itemId, long userId);
    Item get(long itemId);
    List<Item> getAll(long userId);
    void remove(long itemId, long userId);
    List<Item> findByText(String text);
}

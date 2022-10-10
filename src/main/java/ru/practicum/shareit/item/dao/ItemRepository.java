package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository {
    Item create(ItemDto item, long ownerId);

    Item update(ItemDto newItemDto, long itemId, long userId);

    Item get(long itemId);

    List<Item> getAll(long userId);

    void remove(long itemId, long userId);

    List<Item> findByText(String text);
}

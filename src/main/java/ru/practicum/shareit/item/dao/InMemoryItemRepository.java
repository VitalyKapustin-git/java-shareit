package ru.practicum.shareit.item.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
public class InMemoryItemRepository implements ItemRepository {
    private long id;
    private final Map<Long, Item> itemMap;

    InMemoryItemRepository() {
        this.id = 1;
        this.itemMap = new HashMap<>();
    }

    @Override
    public Item create(ItemDto item, long userId) {
        itemMap.put(
                    id,
                    Item.builder()
                            .id(id)
                            .name(item.getName())
                            .description(item.getDescription())
                            .available(item.getAvailable())
                            .ownerId(userId)
                            .build()
                );

        return itemMap.get(id++);
    }

    @Override
    public Item update(ItemDto newItemDto, long itemId, long userId) throws NotOwnerException {
        Item itemToUpdate = itemMap.get(itemId);
        String newName = newItemDto.getName();
        String newDescr = newItemDto.getDescription();
        Boolean newAvail = newItemDto.getAvailable();

        if(userId != itemToUpdate.getOwnerId()) {
            throw new NotOwnerException("You are not an author of the item post!!!");
        }

        if(newName != null) itemToUpdate.setName(newName);
        if(newDescr != null) itemToUpdate.setDescription(newDescr);
        if(newAvail != null) itemToUpdate.setAvailable(newAvail);

        return get(itemId);
    }

    @Override
    public Item get(long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> getAll(long userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public void remove(long itemId, long userId) {
        if (!itemMap.containsKey(itemId)) throw new NotFoundException("item with id " + itemId);

        Item itemToRemove = itemMap.get(itemId);
        if(userId != itemToRemove.getOwnerId()) {
            throw new NotOwnerException("You are not an author of the item post!!!");
        }

        itemMap.remove(itemId);
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isEmpty()) {
            return List.of();
        } else {
            return itemMap.values().stream()
                    .filter(Item::isAvailable)
                    .filter(x -> x.getName().trim().toLowerCase().contains(text.toLowerCase()) ||
                            x.getDescription().trim().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }
}
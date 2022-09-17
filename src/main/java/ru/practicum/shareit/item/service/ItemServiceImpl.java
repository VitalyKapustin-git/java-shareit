package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Primary
@Slf4j
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserService userService;

    @Autowired
    ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item create(ItemDto item, long userId) {
        log.info("[ITEM_SERVICE] Trying to create new item {}", item);
        // Проверка, существует ли пользователь
        userService.get(userId);

        return itemRepository.create(item, userId);
    }

    @Override
    public Item update(ItemDto itemDto, long itemId, long userId) {
        log.info("[ITEM_SERVICE] Trying to update item with id {}", itemId);

        return itemRepository.update(itemDto, itemId, userId);
    }

    @Override
    public Item get(long itemId) {
        log.info("[ITEM_SERVICE] Getting item with id {}", itemId);

        return itemRepository.get(itemId);
    }

    @Override
    public List<Item> getAll(long userId) {
        log.info("[ITEM_SERVICE] Trying to get all items for userId {}", userId);

        return itemRepository.getAll(userId);
    }

    @Override
    public void remove(long itemId, long userId) {
        log.info("[ITEM_SERVICE] Trying to delete item with id {} where authorId is {}", itemId, userId);

        itemRepository.remove(itemId, userId);
    }

    @Override
    public List<Item> findByText(String text) {
        log.info("[ITEM_SERVICE] Trying to find item with pattern {}", text);

        return itemRepository.findByText(text);
    }
}

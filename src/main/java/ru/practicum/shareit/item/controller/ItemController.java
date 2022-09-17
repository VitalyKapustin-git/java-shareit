package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getUserItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        if(userId == null) throw new BadRequestException("Incorrect user id");

        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<Item> findByText(@RequestParam(name = "text") String text) {
        return itemService.findByText(text);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable long itemId) {
        return itemService.get(itemId);
    }

    @PostMapping
    public Item createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        itemService.remove(itemId, userId);
    }
}
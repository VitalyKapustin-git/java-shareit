package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingDto> getUserItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10")
                                                 Integer size) {
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam(name = "text") String text,
                                    @RequestParam(name = "from", defaultValue = "0")
                                    Integer from,
                                    @RequestParam(name = "size", defaultValue = "10")
                                    Integer size) {
        System.out.println(text);
        return itemService.findByText(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(@PathVariable long itemId,
                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.get(itemId, userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return itemService.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        itemService.remove(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody Comment comment) {
        return itemService.addComment(itemId, userId, comment);
    }


}
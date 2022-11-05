package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/items")
@Validated
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "10")
                                               @Positive Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam(name = "text") String text,
                                             @RequestParam(name = "from", defaultValue = "0")
                                             @PositiveOrZero Integer from,
                                             @RequestParam(name = "size", defaultValue = "10")
                                             @Positive Integer size) throws UnsupportedEncodingException {
        return itemClient.findByText(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody Item item,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return itemClient.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        itemClient.deleteItem(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @RequestBody Comment comment) {
        return itemClient.addComment(itemId, userId, comment);
    }


}
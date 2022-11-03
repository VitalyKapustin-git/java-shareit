package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequest,
                                         @RequestHeader(name = "X-Sharer-User-Id") Long requestorId) {
        return itemRequestClient.create(itemRequest, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size,
                                                        @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getOtherUsersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequest(requestId, userId);
    }


}

package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class ItemRequestController {

    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequest,
                                 @RequestHeader(name = "X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.create(itemRequest, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                      @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getOtherUsersRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }


}

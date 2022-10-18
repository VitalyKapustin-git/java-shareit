package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    public ItemRequestDto create(ItemRequest itemRequest, Long requestorId);

    public List<ItemRequestDto> getOwnRequests(Long userId);

    public List<ItemRequestDto> getOtherUsersRequests(Integer from, Integer size, Long userId);

    public ItemRequestDto getRequest(Long requestId, Long userId);

}

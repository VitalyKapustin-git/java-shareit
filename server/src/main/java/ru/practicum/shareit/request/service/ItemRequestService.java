package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequest, Long requestorId);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getOtherUsersRequests(Integer from, Integer size, Long userId);

    ItemRequestDto getRequest(Long requestId, Long userId);

}

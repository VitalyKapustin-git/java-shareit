package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mappers.UserMapper;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {

        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setRequestor(UserMapper.toUser(itemRequestDto.getRequestorDto()));

        return itemRequest;

    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setRequestorDto(itemRequest.getRequestor() == null ? null :
                UserMapper.toUserDto(itemRequest.getRequestor()));
        itemRequestDto.setRequestorId(itemRequest.getRequestor() == null ? 0 : itemRequest.getRequestor().getId());

        return itemRequestDto;

    }

}

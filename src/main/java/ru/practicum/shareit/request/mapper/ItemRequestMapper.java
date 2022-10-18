package ru.practicum.shareit.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapper {

    ItemRepository itemRepository;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestorId(itemRequest.getRequestorId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemRepository.getItemsByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())
        );

        return itemRequestDto;

    }


}

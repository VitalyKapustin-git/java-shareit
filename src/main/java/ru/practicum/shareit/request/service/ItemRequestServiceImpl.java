package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserService userService;

    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId) {

        // Проверка, существует ли пользователь
        UserDto requestorDto = userService.get(requestorId);

        itemRequestDto.setRequestorId(requestorId);
        itemRequestDto.setRequestorDto(requestorDto);

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto));

        itemRequestDto.setId(itemRequest.getId());
        setItemsToItemRequest(List.of(itemRequestDto));

        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {

        log.info("[ITEM_SERVICE] Trying to get requests for user with id {}", userId);
        // Проверка, существует ли пользователь
        userService.get(userId);

        List<ItemRequestDto> itemRequests = itemRequestRepository.getItemRequestsByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        setItemsToItemRequest(itemRequests);

        return itemRequests;

    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Integer from, Integer size, Long userId) {

        // Проверка, существует ли пользователь
        userService.get(userId);

        int fromPage = from / size;

        Pageable pageable = PageRequest.of(fromPage, size);

        List<ItemRequestDto> itemRequests = itemRequestRepository.getItemRequestsByRequestorIdNot(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        setItemsToItemRequest(itemRequests);

        return itemRequests;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {

        // Проверка, существует ли пользователь
        userService.get(userId);

        ItemRequest itemRequest = itemRequestRepository.getItemRequestById(requestId);

        if (itemRequest == null) throw new NotFoundException("Not found request with id " + requestId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.getItemsByRequestId(itemRequestDto.getId());

        if (items.size() > 0) {

            itemRequestDto.setItems(items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList())
            );

        }

        return itemRequestDto;
    }

    private void setItemsToItemRequest(List<ItemRequestDto> itemRequests) {

        List<Long> itemRequestsId = itemRequests.stream().map(ItemRequestDto::getId).collect(Collectors.toList());

        Map<Long, List<ItemDto>> requestsAndItems = itemRepository.getItemsByRequestIdIn(itemRequestsId).stream()
                .map(ItemMapper::toItemDto)
                .collect(groupingBy(ItemDto::getRequestId));

        itemRequests.forEach(itemRequest -> itemRequest.setItems(

                requestsAndItems.get(itemRequest.getId()) != null ?
                        requestsAndItems.get(itemRequest.getId()) : List.of()

        ));

    }

}

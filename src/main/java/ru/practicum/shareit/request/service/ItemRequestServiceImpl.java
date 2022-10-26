package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;

    UserService userService;

    ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequest itemRequest, Long requestorId) {

        if (itemRequest.getDescription().isBlank()) throw new BadRequestException("Description couldn't be empty!");

        // Проверка, существует ли пользователь
        userService.get(requestorId);
        itemRequest.setRequestorId(requestorId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
        itemRequestDto.setItems(itemRepository.getItemsByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())
        );

        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {

        log.info("[ITEM_SERVICE] Trying to get requests for user with id {}", userId);
        // Проверка, существует ли пользователь
        userService.get(userId);

        return itemRequestRepository.getItemRequestsByRequestorId(userId).stream()
                .map(v -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(v);
                    itemRequestDto.setItems(itemRepository.getItemsByRequestId(v.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Integer from, Integer size, Long userId) {

        // Проверка, существует ли пользователь
        userService.get(userId);

        int fromPage = from / size;

        Pageable pageable = PageRequest.of(fromPage, size);

        return itemRequestRepository.getItemRequestsByRequestorIdNot(userId, pageable)
                .stream()
                .map(v -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(v);
                    itemRequestDto.setItems(itemRepository.getItemsByRequestId(v.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList()));

                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {

        // Проверка, существует ли пользователь
        userService.get(userId);

        ItemRequest itemRequest = itemRequestRepository.getItemRequestById(requestId);
        if (itemRequest == null) throw new NotFoundException("Not found request with id " + requestId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.getItemsByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())
        );

        return itemRequestDto;
    }

}

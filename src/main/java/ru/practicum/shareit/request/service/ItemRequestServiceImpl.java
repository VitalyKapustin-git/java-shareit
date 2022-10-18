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

    ItemRequestMapper itemRequestMapper;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequest itemRequest, Long requestorId) {

        if (itemRequest.getDescription().isBlank()) throw new BadRequestException("Description couldn't be empty!");

        // Проверка, существует ли пользователь
        userService.get(requestorId);
        itemRequest.setRequestorId(requestorId);

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {

        log.info("[ITEM_SERVICE] Trying to get requests for user with id {}", userId);
        // Проверка, существует ли пользователь
        userService.get(userId);

        return itemRequestRepository.getItemRequestsByRequestorId(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
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
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {

        // Проверка, существует ли пользователь
        userService.get(userId);

        ItemRequest itemRequest = itemRequestRepository.getItemRequestById(requestId);
        if (itemRequest == null) throw new NotFoundException("Not found request with id " + requestId);

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }
}

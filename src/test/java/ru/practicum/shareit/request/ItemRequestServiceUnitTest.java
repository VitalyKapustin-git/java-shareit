package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserServiceImpl userService;

    @Test
    public void should_CreateRequestForItem() {

        User user = new User();
        user.setId(1);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("Нужна дрель!");
        itemRequestDto.setRequestorDto(new UserDto());
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setRequestorId(1L);

        Item item = new Item();
        item.setName("Дрель-шуроповерт");
        item.setRequestId(itemRequestDto.getId());

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        Mockito
                .when(itemRepository.getItemsByRequestIdIn(List.of(itemRequest.getId())))
                .thenReturn(List.of(item));


        ItemRequestDto itemRequestDto2 = itemRequestService.create(itemRequestDto, 1L);

        Assertions.assertEquals(item.getName(), itemRequestDto2.getItems().get(0).getName());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto2.getDescription());

    }

    @Test
    public void should_GetOwnRequests() {

        User user = new User();
        user.setName("me");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestor(user);
        itemRequest1.setDescription("fdfdfdf");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setRequestor(user);
        itemRequest2.setDescription("fdfdfdf");

        Item item1 = new Item();
        item1.setId(1);
        item1.setRequestId(itemRequest1.getId());

        Item item2 = new Item();
        item2.setId(3);
        item2.setRequestId(itemRequest2.getId());

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.getItemRequestsByRequestorId(1L))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        Mockito
                .when(itemRepository.getItemsByRequestIdIn(Mockito.anyList()))
                .thenReturn(List.of(item1, item2));

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(1L);

        Assertions.assertTrue(ownRequests.stream().map(ItemRequestDto::getId)
                .collect(Collectors.toList())
                .contains(itemRequest2.getId()));

    }

    @Test
    public void should_OtherUsersRequests() {

        User user = new User();
        user.setName("me");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestor(user);
        itemRequest1.setDescription("fdfdfdf");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setRequestor(user);
        itemRequest2.setDescription("fdfdfdf");

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.getItemRequestsByRequestorIdNot(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        List<ItemRequestDto> ownRequests = itemRequestService.getOtherUsersRequests(1, 2, 2L);

        Assertions.assertTrue(ownRequests.stream().map(ItemRequestDto::getId)
                .collect(Collectors.toList())
                .contains(itemRequest2.getId()));

    }

    @Test
    public void should_GetRequestById() {

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestor(new User());

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.getItemRequestById(Mockito.anyLong()))
                .thenReturn(itemRequest1);

        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(new Item()));

        Assertions.assertEquals(itemRequest1.getId(),
                itemRequestService.getRequest(1L, 1L).getId());

    }


}

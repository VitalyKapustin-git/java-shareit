package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

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

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("Нужна дрель!");
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setName("Дрель-шуроповерт");

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);

        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item));


        ItemRequestDto itemRequestDto = itemRequestService.create(itemRequest, 1L);

        Assertions.assertEquals(item.getName(), itemRequestDto.getItems().get(0).getName());
        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());

    }

    @Test
    public void should_ThrowException_OnEmptyDescription() {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("");
        itemRequest.setCreated(LocalDateTime.now());

        try {
            itemRequestService.create(itemRequest, 1L);
        } catch (BadRequestException e) {
            Assertions.assertEquals("Description couldn't be empty!", e.getMessage());
        }

    }

    @Test
    public void should_GetOwnRequests() {

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestorId(1L);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setRequestorId(1L);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(new Item()));

        Mockito
                .when(itemRequestRepository.getItemRequestsByRequestorId(1L))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(1L);

        Assertions.assertTrue(ownRequests.stream().map(ItemRequestDto::getId)
                .collect(Collectors.toList())
                .contains(itemRequest2.getId()));

    }

    @Test
    public void should_OtherUsersRequests() {

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestorId(1L);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2);
        itemRequest2.setRequestorId(1L);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.getItemRequestsByRequestorIdNot(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(new Item()));

        List<ItemRequestDto> ownRequests = itemRequestService.getOtherUsersRequests(1, 2, 2L);

        Assertions.assertTrue(ownRequests.stream().map(ItemRequestDto::getId)
                .collect(Collectors.toList())
                .contains(itemRequest2.getId()));

    }

    @Test
    public void should_GetRequestById() {

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1);
        itemRequest1.setRequestorId(1L);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(new UserDto());

        Mockito
                .when(itemRequestRepository.getItemRequestById(Mockito.anyLong()))
                .thenReturn(itemRequest1);

        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(new Item()));

        Assertions.assertEquals(itemRequest1.getRequestorId(),
                itemRequestService.getRequest(1L, 1L).getRequestorId());

    }


}

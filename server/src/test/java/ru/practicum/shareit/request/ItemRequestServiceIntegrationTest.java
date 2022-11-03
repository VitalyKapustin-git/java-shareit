package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceIntegrationTest {

    private final ItemRequestService itemRequestService;

    private final UserService userService;

    ItemRequestDto itemRequest;

    User itemRequestor;

    @BeforeAll
    public void setUp() {

        itemRequestor = new User();
        itemRequestor.setName("requestor");
        itemRequestor.setEmail("req@ex.com");

        userService.create(itemRequestor);

        itemRequest = new ItemRequestDto();
        itemRequest.setRequestorId(itemRequestor.getId());
        itemRequest.setDescription("Очень нужна дрель!");

    }

    @Test
    public void should_CreateRequestForItem() {

        itemRequestService.create(itemRequest, itemRequestor.getId());

        ItemRequestDto itemRequestDto = itemRequestService.getRequest(itemRequest.getId(), itemRequestor.getId());

        Assertions.assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());

    }

    @Test
    public void should_ReturnOwnRequests() {

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(itemRequestor.getId());

        Assertions.assertEquals(itemRequest.getDescription(), ownRequests.get(0).getDescription());

    }

}

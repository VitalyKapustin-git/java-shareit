package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ItemServiceIntegrationTest {

    private final ItemService itemService;

    private final UserService userService;

    @MockBean
    BookingRepository bookingRepository;

    private User owner;

    private Item item;

    private User user2;

    private UserDto user2Dto;

    private UserDto userDto;

    private ItemDto itemDto;

    @BeforeAll
    public void setUp() {

        owner = new User();
        owner.setName("Виталий");
        owner.setEmail("workgroupkap@gmail.com");

        userDto = userService.create(owner);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Самая лучшая дрель в мире!");
        item.setOwnerId(userDto.getId());
        item.setAvailable(true);

        itemDto = itemService.create(item, userDto.getId());

    }

    @AfterAll
    public void afterAll() {

        itemService.remove(item.getId(), owner.getId());
        userService.remove(owner.getId());
        userService.remove(user2.getId());

    }

    @Test
    @Order(1)
    public void should_getUserItems() {

        ItemWithBookingDto userItemDto = itemService.get(itemDto.getId(), userDto.getId());

        Assertions.assertEquals(owner.getId(), userItemDto.getOwnerId());
        Assertions.assertEquals(item.getDescription(), userItemDto.getDescription());

    }

    @Test
    public void should_AddCommentToItem() {

        user2 = new User();
        user2.setName("Ревностный комментатор");
        user2.setEmail("super_commentator@test.ru");

        user2Dto = userService.create(user2);

        Comment comment = new Comment();
        comment.setText("Супер пупер дрель буду брать еще!");
        comment.setAuthorId(user2Dto.getId());
        comment.setItemId(itemDto.getId());

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(item);

        LocalDateTime localDateTime = LocalDateTime.now();

        Mockito
                .when(bookingRepository.getPastBookings(user2.getId()))
                .thenReturn(List.of(booking));

        CommentDto commentDto = itemService.addComment(itemDto.getId(), user2.getId(), comment);
        ItemWithBookingDto userItemDto = itemService.get(itemDto.getId(), itemDto.getId());

        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(user2Dto.getName(), userItemDto.getComments().get(0).getAuthorName());
        Assertions.assertEquals(localDateTime.getYear(), commentDto.getCreated().getYear());
        Assertions.assertEquals(localDateTime.getDayOfMonth(), commentDto.getCreated().getDayOfMonth());
        Assertions.assertEquals(localDateTime.getMonth(), commentDto.getCreated().getMonth());

    }

}

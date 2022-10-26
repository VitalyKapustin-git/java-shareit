package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;
    @Mock
    UserServiceImpl userService;

    Item oldItem = new Item();

    @BeforeEach
    public void setUp() {
        oldItem.setId(1);
        oldItem.setName("item");
        oldItem.setOwnerId(2);
        oldItem.setAvailable(true);
        oldItem.setDescription("azaza");
        oldItem.setRequestId(3L);
    }

    @Test
    public void testCreateItem() {

        Item item = oldItem;

        oldItem.setOwnerId(0);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(UserMapper.toUserDto(new User()));

        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);

        Assertions.assertEquals(itemService.create(item, 2).getOwnerId(), 2);

    }

    @Test
    public void findByText() {

        Mockito
                .when(itemRepository.findByText(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(oldItem));

        Assertions.assertEquals(oldItem.getDescription(), itemService.findByText("azaza", 1, 1)
                .get(0).getDescription());

    }

    @Test
    public void findByTextByBlank() {

        Assertions.assertEquals(0, itemService.findByText("", 1, 1)
                .size());

    }

    @Test
    public void getAll() {

        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();
        itemWithBookingDto.setName("check");

        Mockito
                .when(itemRepository.getItemsByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(oldItem));

        Mockito
                .when(itemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(oldItem);

        Assertions.assertEquals(oldItem.getName(), itemService.getAll(2L, 2, 2).get(0).getName());

    }

    @Test
    public void get() {

        Mockito
                .when(itemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(null);

        try {
            itemService.get(1, 1);
        } catch (NotFoundException e) {
            Assertions.assertEquals("itemId: 1", e.getMessage());
        }

    }

    @Test
    public void testUpdateItem() {

        Mockito
                .when(itemRepository.getItemById(1))
                .thenReturn(oldItem);

        Mockito
                .when(itemRepository.save(oldItem))
                .thenReturn(oldItem);

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("newItemName");
        newItemDto.setDescription("newDescr");

        itemService.update(newItemDto, 1, 2);

        Assertions.assertEquals(newItemDto.getName(), oldItem.getName());
        Assertions.assertEquals(newItemDto.getDescription(), oldItem.getDescription());

    }

    @Test
    public void mustThrowOnItemUpdateWhenNotOwner() {

        Mockito
                .when(itemRepository.getItemById(1))
                .thenReturn(oldItem);

        ItemDto newItemDto = new ItemDto();

        try {
            itemService.update(newItemDto, 1, 3);
        } catch (NotOwnerException e) {
            Assertions.assertEquals(e.getMessage(), "You are not an author of the item post!!!");
        }

    }

    @Test
    public void testGetItem() {

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2021, 2, 2, 10, 0));
        booking1.setEnd(LocalDateTime.of(2021, 2, 3, 10, 0));

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.of(2022, 10, 18, 10, 0));
        booking2.setEnd(LocalDateTime.of(2022, 10, 19, 10, 0));

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.of(2022, 12, 2, 10, 0));
        booking3.setEnd(LocalDateTime.of(2022, 12, 3, 10, 0));

        Booking booking4 = new Booking();
        booking4.setStart(LocalDateTime.of(2023, 2, 2, 10, 0));
        booking4.setEnd(LocalDateTime.of(2023, 2, 3, 10, 0));

        Mockito
                .when(itemRepository.getItemById(1))
                .thenReturn(oldItem);

        Mockito
                .when(bookingRepository.getBookingsByItemId(oldItem.getId()))
                .thenReturn(
                        List.of(
                                booking1, booking2, booking3, booking4
                        )
                );

        Mockito
                .when(commentRepository.getAllItemComments(1))
                .thenReturn(List.of());

        Assertions.assertEquals(LocalDateTime.of(2022, 10, 19, 10, 0),
                itemService.get(1, 2).getLastBooking().getEnd());

        Assertions.assertEquals(LocalDateTime.of(2022, 12, 2, 10, 0),
                itemService.get(1, 2).getNextBooking().getStart());

    }

    @Test
    public void testAddCommentToItem() {

        Comment comment = new Comment();
        comment.setText("mycomment");

        Booking booking = new Booking();
        booking.setItemId(1);

        Item item = new Item();
        item.setName("testItemName");

        UserDto user = new UserDto();
        user.setName("testAuthorName");

        Mockito
                .when(bookingRepository.getPastBookings(2L))
                .thenReturn(List.of(booking));

        Mockito
                .when(commentRepository.save(comment))
                .thenReturn(comment);

        Mockito
                .when(itemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(item);

        Mockito
                .when(userService.get(Mockito.anyLong()))
                .thenReturn(user);


        CommentDto commentDto = itemService.addComment(1, 2, comment);

        Assertions.assertEquals(1, comment.getItemId());
        Assertions.assertEquals(2, comment.getAuthorId());
        Assertions.assertEquals("testItemName", commentDto.getItemName());

    }

    @Test
    public void testAddCommentToItemWithEmptyText() {

        Comment comment = new Comment();
        comment.setText("    ");

        Mockito
                .lenient()
                .when(commentRepository.save(comment))
                .thenReturn(comment);

        try {
            itemService.addComment(1, 2, comment);
        } catch (BadRequestException e) {
            Assertions.assertEquals("comment couldn't be empty.", e.getMessage());
        }

    }

    @Test
    public void testAddCommentToItemWhenNotBookedFullTime() {

        Comment comment = new Comment();
        comment.setText("mycomment");

        Booking booking = new Booking();
        booking.setItemId(1);

        Mockito
                .when(bookingRepository.getPastBookings(2L))
                .thenReturn(List.of());

        try {
            itemService.addComment(1, 2, comment);
        } catch (BadRequestException e) {
            Assertions.assertEquals("you must use booked item" +
                    " for full time period before leave comment", e.getMessage());
        }

    }

}

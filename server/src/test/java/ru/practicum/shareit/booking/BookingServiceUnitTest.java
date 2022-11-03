package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    Pageable pageable;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0 / 5, 5);
    }

    @Test
    public void should_CreateBooking() {

        Item item = new Item();
        item.setId(3);
        item.setOwnerId(5);
        item.setAvailable(true);

        User booker = new User();
        booker.setId(2);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookingApproved("APPROVED");
        booking.setStart(LocalDateTime.of(2030, 2, 2, 10, 0));
        booking.setEnd(LocalDateTime.of(2030, 2, 3, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(2))
                .thenReturn(booker);

        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        bookingService.createBooking(booker.getId(), booking);

        Assertions.assertEquals(booker.getId(), booking.getBooker().getId());

    }

    @Test
    public void should_ThrowException_when_CreateBookingWithNonExistingBooker() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());

        Item item = new Item();

        Mockito
                .when(itemRepository.getItemById(booking.getItem().getId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(null);

        try {
            bookingService.createBooking(65454, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Not found user.", e.getMessage());
        }

    }

    @Test
    public void should_ThrowException_when_CreateBookingForNonExistingItem() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(null);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(new User());

        try {
            bookingService.createBooking(65454, booking);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Not found item for booking.", e.getMessage());
        }

    }

    @Test
    public void should_ThrowException_when_CreateBookingForNonAvailableItem() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());

        Item item = new Item();
        item.setAvailable(false);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(new User());

        try {
            bookingService.createBooking(65454, booking);
        } catch (BadRequestException e) {
            Assertions.assertEquals("Item not available!", e.getMessage());
        }
    }

    @Test
    public void should_ThrowException_when_CreateBookingWhenEndBeforeStart() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());
        booking.setStart(LocalDateTime.of(2030, 2, 3, 10, 0));
        booking.setEnd(LocalDateTime.of(2030, 2, 2, 10, 0));

        Item item = new Item();
        item.setAvailable(true);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(new User());

        try {
            bookingService.createBooking(65454, booking);
        } catch (BadRequestException e) {
            Assertions.assertEquals("End of booking could not be before start.", e.getMessage());
        }
    }

    @Test
    public void should_ThrowException_when_CreateBookingWhenStartInPast() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());
        booking.setStart(LocalDateTime.of(2017, 2, 3, 10, 0));
        booking.setEnd(LocalDateTime.of(2030, 2, 2, 10, 0));

        Item item = new Item();
        item.setAvailable(true);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(new User());

        try {
            bookingService.createBooking(65454, booking);
        } catch (BadRequestException e) {
            Assertions.assertEquals("Start and end of booking could not be in the past.", e.getMessage());
        }
    }

    @Test
    public void should_ThrowException_when_CreateBookingWhenItemAlreadyBooked() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());
        booking.setStart(LocalDateTime.of(2029, 2, 3, 10, 0));
        booking.setEnd(LocalDateTime.of(2030, 2, 2, 10, 0));

        Item item = new Item();
        item.setAvailable(true);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(65454))
                .thenReturn(new User());

        Mockito
                .when(bookingRepository.getCrossBookingsForItem(booking.getStart(), booking.getItemId()))
                .thenReturn(List.of(new Booking()));

        try {
            bookingService.createBooking(65454, booking);
        } catch (BadRequestException e) {
            Assertions.assertEquals("Already booked item with id: " + booking.getItemId(), e.getMessage());
        }
    }


    @Test
    public void should_ThrowException_onReturnBooking_when_BookingNotExist() {

        Mockito
                .lenient()
                .when(bookingRepository.getBookingById(Mockito.anyInt()))
                .thenReturn(null);

        try {
            bookingService.getBooking(1, 1);
        } catch (NotFoundException e) {
            Assertions.assertEquals("bookingId: " + 1, e.getMessage());
        }

    }

    @Test
    public void should_ThrowException_onReturnBooking_when_RequestNotOwnerOrAuthor() {

        User booker = new User();
        booker.setId(343);

        Item item = new Item();
        item.setOwnerId(543653423);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setId(1);

        Mockito
                .lenient()
                .when(bookingRepository.getBookingById(booking.getId()))
                .thenReturn(booking);

        Mockito
                .when(itemRepository.getItemById(booking.getItem().getId()))
                .thenReturn(item);

        try {
            bookingService.getBooking(1, 1);
        } catch (NotFoundException e) {
            Assertions.assertEquals("any booking for userId: " + 1, e.getMessage());
        }

    }

    @Test
    public void should_ReturnBookingById() {

        User owner = new User();
        owner.setId(242342332);

        User booker = new User();
        booker.setId(3131);

        Item item = new Item();
        item.setName("ololo");
        item.setOwnerId(owner.getId());

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setId(1);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2023, 10, 20, 10, 10));
        booking.setBookingApproved("CURRENT");

        Mockito
                .when(bookingRepository.getBookingById(booking.getId()))
                .thenReturn(booking);

        Mockito
                .lenient()
                .when(itemRepository.getItemById(booking.getItem().getId()))
                .thenReturn(item);

        Mockito
                .lenient()
                .when(userRepository.getUserById(booking.getBooker().getId()))
                .thenReturn(booker);

        BookingDto bookingDto = bookingService.getBooking(1, booker.getId());

        Assertions.assertEquals("ololo", bookingDto.getItem().getName());
        Assertions.assertNotNull(bookingDto.getBooker());
        Assertions.assertEquals(booking.getItemId(), bookingDto.getItem().getId());

    }

    @Test
    public void should_ThrowException_onGetAllBookings_when_RequestForNonExistedUser() {

        Mockito
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(null);

        try {
            bookingService.getAllBookings("CURRENT", 24232, 0, 5);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Not found user.", e.getMessage());
        }

    }

    @Test
    public void should_ThrowException_onGetOwnerBookings_when_NoBookings() {

        Mockito
                .when(itemRepository.getAllUserItemsId(Mockito.anyLong()))
                .thenReturn(List.of());

        try {
            bookingService.getOwnerBookings("CURRENT", 24232, 0, 5);
        } catch (NotFoundException e) {
            Assertions.assertEquals("No bookings for this owner", e.getMessage());
        }

    }

    @Test
    public void should_ReturnCorrectBookingDtoFromAllBookings() {

        Item item = new Item();
        item.setName("zprs");

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(item);
        booking.setId(1);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2023, 10, 20, 10, 10));
        booking.setBookingApproved("CURRENT");

        List<Booking> n = new ArrayList<>();
        n.add(booking);

        Mockito
                .when(bookingRepository.getCurrentBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(bookingRepository.getPastBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(bookingRepository.getFutureBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(bookingRepository.getWaitingBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(bookingRepository.getRejectedBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(bookingRepository.getBookingsByBooker_IdOrderByStartDesc(Mockito.anyLong(),
                        Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .lenient()
                .when(itemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(item);

        Mockito
                .lenient()
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(new User());


        BookingDto bookingDto = bookingService.getAllBookings("CURRENT", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto1 = bookingService.getAllBookings("PAST", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto1.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto1.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto1.getItem().getName());


        BookingDto bookingDto2 = bookingService.getAllBookings("FUTURE", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto2.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto2.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto2.getItem().getName());


        BookingDto bookingDto3 = bookingService.getAllBookings("WAITING", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto3.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto3.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto3.getItem().getName());


        BookingDto bookingDto4 = bookingService.getAllBookings("REJECTED", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto4.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto4.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto4.getItem().getName());


        BookingDto bookingDto5 = bookingService.getAllBookings("ALL", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto5.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto5.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto5.getItem().getName());

    }

    @Test
    public void should_ReturnCorrectBookingDtoFromOwnerBookings() {

        Item item = new Item();
        item.setName("zprs");

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(item);
        booking.setId(1);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2023, 10, 20, 10, 10));
        booking.setBookingApproved("CURRENT");

        List<Booking> n = new ArrayList<>();
        n.add(booking);

        Mockito
                .lenient()
                .when(itemRepository.getAllUserItemsId(Mockito.anyLong()))
                .thenReturn(List.of(1L));

        Mockito
                .lenient()
                .when(itemRepository.getItemById(booking.getItem().getId()))
                .thenReturn(item);

        Mockito
                .when(bookingRepository.getCurrentOwnerBookings(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.getPastOwnerBookings(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.getFutureOwnerBookings(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.getWaitingOwnerBookings(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.getRejectedOwnerBookings(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.getBookingsByItem_IdInOrderByStartDesc(Mockito.anyList(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(booking));


        BookingDto bookingDto = bookingService.getOwnerBookings("CURRENT", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto1 = bookingService.getOwnerBookings("PAST", 24232, 0, 5).get(0);

        Assertions.assertEquals(bookingDto1.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingDto1.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto2 = bookingService.getOwnerBookings("FUTURE", 24232, 0, 5).get(0);

        Assertions.assertEquals(bookingDto2.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingDto2.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto3 = bookingService.getOwnerBookings("WAITING", 24232, 0, 5).get(0);

        Assertions.assertEquals(bookingDto3.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingDto3.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto4 = bookingService.getOwnerBookings("REJECTED", 24232, 0, 5).get(0);

        Assertions.assertEquals(bookingDto4.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingDto4.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

        BookingDto bookingDto5 = bookingService.getOwnerBookings("ALL", 24232, 0, 5).get(0);

        Assertions.assertEquals(bookingDto5.getStart(), bookingDto.getStart());
        Assertions.assertEquals(bookingDto5.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());


    }

    @Test
    public void should_addApproveToBooking() {

        User user = new User();
        user.setId(1);
        user.setName("sadsad");
        user.setEmail("fsdf@yfds.df");

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());
        booking.setItemId(1);
        booking.setBooker(user);

        Item item = new Item();
        item.setId(1);
        item.setOwnerId(1);

        Mockito
                .when(bookingRepository.getBookingById(Mockito.anyLong()))
                .thenReturn(booking);

        Mockito
                .when(itemRepository.getItemById(booking.getItem().getId()))
                .thenReturn(item);

        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.setApprove(true, 1, 1);

        Assertions.assertEquals("APPROVED", bookingDto.getStatus());
        Assertions.assertEquals(user.getId(), booking.getBooker().getId());

    }

    @Test
    public void should_ThrowException_onSetApprove_when_ApprovedBooking() {

        Booking booking = new Booking();
        booking.setBooker(new User());
        booking.setItem(new Item());
        booking.setBookingApproved("APPROVED");

        Mockito
                .when(bookingRepository.getBookingById(Mockito.anyLong()))
                .thenReturn(booking);

        try {
            bookingService.setApprove(true, 1, 1);
        } catch (BadRequestException e) {
            Assertions.assertEquals("No any not approved bookings for userId: 1", e.getMessage());
        }

    }

    @Test
    public void should_ReturnBookingDto() {

        User booker = new User();
        booker.setId(1);
        booker.setName("Booker User");
        booker.setEmail("test@example.com");

        Item item = new Item();
        item.setId(1);
        item.setName("Дрель");
        item.setDescription("Дрель!");

        Booking booking = new Booking();
        booking.setId(1);
        booking.setItemId(1);
        booking.setBookingApproved("APPROVED");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(LocalDateTime.now().plusMonths(2));
        booking.setBooker(booker);
        booking.setItem(item);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        Assertions.assertEquals(bookingDto.getItem().getClass(), ItemDto.class);
        Assertions.assertEquals(bookingDto.getBooker().getClass(), UserDto.class);
        Assertions.assertNotEquals("UserDto", bookingDto.getBooker().getClass().getName());

    }

    @Test
    public void should_ReturnBookingItemDto() {

        User booker = new User();
        booker.setId(1);
        booker.setName("Booker User");
        booker.setEmail("test@example.com");

        Item item = new Item();
        item.setId(1);
        item.setName("Дрель");
        item.setDescription("Дрель!");

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookingApproved("APPROVED");
        booking.setStart(LocalDateTime.now().plusMonths(1));
        booking.setEnd(LocalDateTime.now().plusMonths(2));
        booking.setBooker(booker);
        booking.setItem(item);

        BookingItemDto bookingItemDto = BookingMapper.toBookingItemDto(booking);

        Assertions.assertEquals(bookingItemDto.getItemId(), booking.getItem().getId());
        Assertions.assertEquals(bookingItemDto.getBookerId(), booking.getBooker().getId());

    }


}

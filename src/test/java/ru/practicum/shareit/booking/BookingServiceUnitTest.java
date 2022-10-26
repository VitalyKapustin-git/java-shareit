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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
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
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookingApproved("APPROVED");
        booking.setStart(LocalDateTime.of(2030, 2, 2, 10, 0));
        booking.setEnd(LocalDateTime.of(2030, 2, 3, 10, 0));
        booking.setItemId(3);

        Item item = new Item();
        item.setId(3);
        item.setOwnerId(5);
        item.setAvailable(true);

        User booker = new User();
        booker.setId(2);

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

        Assertions.assertEquals(booker.getId(), booking.getBookerId());

    }

    @Test
    public void should_ThrowException_when_CreateBookingWithNonExistingBooker() {

        Booking booking = new Booking();

        Item item = new Item();

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
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

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookerId(24232);

        Item item = new Item();
        item.setOwnerId(543653423);

        Mockito
                .lenient()
                .when(bookingRepository.getBookingById(booking.getId()))
                .thenReturn(booking);

        Mockito
                .when(itemRepository.getItemById(booking.getId()))
                .thenReturn(item);

        try {
            bookingService.getBooking(1, 1);
        } catch (NotFoundException e) {
            Assertions.assertEquals("any booking for userId: " + 1, e.getMessage());
        }

    }

    @Test
    public void should_ReturnBookingById() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookerId(24232);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2023, 10, 20, 10, 10));
        booking.setBookingApproved("CURRENT");

        Item item = new Item();
        item.setName("ololo");

        Mockito
                .when(bookingRepository.getBookingById(1))
                .thenReturn(booking);

        Mockito
                .when(itemRepository.getItemById(booking.getItemId()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(booking.getBookerId()))
                .thenReturn(new User());

        BookingDto bookingDto = bookingService.getBooking(1, 24232);

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

        Booking booking = new Booking();
        booking.setId(1);
        booking.setBookerId(24232);
        booking.setStart(LocalDateTime.of(2023, 10, 10, 10, 10));
        booking.setEnd(LocalDateTime.of(2023, 10, 20, 10, 10));
        booking.setBookingApproved("CURRENT");

        List<Booking> n = new ArrayList<>();
        n.add(booking);

        Item item = new Item();
        item.setName("zprs");

        Mockito
                .when(bookingRepository.getCurrentBookings(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(n);

        Mockito
                .when(itemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(new User());


        BookingDto bookingDto = bookingService.getAllBookings("CURRENT", 24232, 0, 5).get(0);

        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(item.getName(), bookingDto.getItem().getName());

    }

    @Test
    public void should_addApproveToBooking() {

        Booking booking = new Booking();
        booking.setItemId(1);
        booking.setBookerId(1);

        User user = new User();
        user.setId(1);
        user.setName("sadsad");
        user.setEmail("fsdf@yfds.df");

        Item item = new Item();
        item.setId(1);
        item.setOwnerId(1);

        Mockito
                .when(bookingRepository.getBookingById(Mockito.anyLong()))
                .thenReturn(booking);

        Mockito
                .when(itemRepository.getItemById(1))
                .thenReturn(item);

        Mockito
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(user);

        BookingDto bookingDto = bookingService.setApprove(true, 1, 1);

        Assertions.assertEquals("APPROVED", bookingDto.getStatus());
        Assertions.assertEquals(user.getId(), booking.getBookerId());

    }

    @Test
    public void should_ThrowException_onSetApprove_when_ApprovedBooking() {

        Booking booking = new Booking();
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


}

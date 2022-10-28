package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceJPATest {

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    User itemOwner;

    User itemBooker;

    Item item;

    @BeforeEach
    public void beforeEach() {

        itemOwner = new User();
        itemOwner.setName("Vitaly");
        itemOwner.setEmail("adse@sa.rn");

        itemBooker = new User();
        itemBooker.setName("Бронировальщик");
        itemBooker.setEmail("booker@example.com");

        item = new Item();
        item.setDescription("Дрель обычная");
        item.setName("Дрель");
        item.setAvailable(true);

        userRepository.save(itemOwner);
        userRepository.save(itemBooker);
        item.setOwnerId(itemOwner.getId());
        itemRepository.save(item);

    }

    @AfterAll
    public void afterEach() {

        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

    }

    @Test
    public void should_ReturnPastBookings() throws InterruptedException {

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBookingApproved("PAST");
        pastBooking.setBooker(itemBooker);
        pastBooking.setStart(LocalDateTime.now().plusSeconds(2));
        pastBooking.setEnd(LocalDateTime.now().plusSeconds(3));

        bookingRepository.save(pastBooking);

        Thread.sleep(4000);

        List<Booking> pastBookings = bookingRepository.getPastBookings(itemBooker.getId(), Pageable.unpaged());

        Assertions.assertEquals(pastBooking.getStart(), pastBookings.get(0).getStart());

    }

    @Test
    public void should_ReturnFutureBookings() {

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBookingApproved("FUTURE");
        futureBooking.setBooker(itemBooker);
        futureBooking.setStart(LocalDateTime.now().plusYears(1));
        futureBooking.setEnd(LocalDateTime.now().plusYears(2));

        bookingRepository.save(futureBooking);

        List<Booking> futureBookings = bookingRepository.getFutureBookings(itemBooker.getId(), Pageable.unpaged());

        Assertions.assertEquals(futureBooking.getStart(), futureBookings.get(0).getStart());

    }

    @Test
    public void should_ReturnWaitingBookings() {

        Booking waitingBooking = new Booking();
        waitingBooking.setItem(item);
        waitingBooking.setBooker(itemBooker);
        waitingBooking.setStart(LocalDateTime.now().plusMonths(1));
        waitingBooking.setEnd(LocalDateTime.now().plusMonths(2));

        bookingRepository.save(waitingBooking);

        List<Booking> waitingBookings = bookingRepository.getWaitingBookings(itemBooker.getId(), Pageable.unpaged());

        Assertions.assertEquals(waitingBooking.getStart(), waitingBookings.get(0).getStart());

    }


    @Test
    public void should_ReturnRejectedBookings() {

        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item);
        rejectedBooking.setBookingApproved("REJECTED");
        rejectedBooking.setBooker(itemBooker);
        rejectedBooking.setStart(LocalDateTime.now().plusYears(2));
        rejectedBooking.setEnd(LocalDateTime.now().plusYears(3));

        bookingRepository.save(rejectedBooking);

        List<Booking> rejectedBookings = bookingRepository.getRejectedBookings(itemBooker.getId(), Pageable.unpaged());

        Assertions.assertEquals(rejectedBooking.getStart(), rejectedBookings.get(0).getStart());
    }

    @Test
    public void should_ReturnCurrentBookings() throws InterruptedException {

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBookingApproved("CURRENT");
        currentBooking.setBooker(itemBooker);
        currentBooking.setStart(LocalDateTime.now().plusSeconds(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(5));

        bookingRepository.save(currentBooking);

        Thread.sleep(2000);

        List<Booking> currentBookings = bookingRepository.getCurrentBookings(itemBooker.getId(), Pageable.unpaged());

        Assertions.assertEquals(currentBooking.getStart(), currentBookings.get(0).getStart());

    }

}




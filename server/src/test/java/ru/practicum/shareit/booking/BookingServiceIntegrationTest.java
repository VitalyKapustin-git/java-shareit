package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;

    private final UserService userService;

    private final ItemService itemService;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @AfterAll
    public void afterAll() {

        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();

    }

    @Test
    public void should_returnBookingList() {

        User itemOwner = new User();
        itemOwner.setName("Vitaly");
        itemOwner.setEmail("adse@sa.rn");

        User itemBooker = new User();
        itemBooker.setName("Бронировальщик");
        itemBooker.setEmail("booker@example.com");

        Item item = new Item();
        item.setDescription("Дрель обычная");
        item.setName("Дрель");
        item.setAvailable(true);

        userService.create(itemOwner);
        userService.create(itemBooker);
        itemService.create(item, itemOwner.getId());

        Booking futureBooking = new Booking();
        futureBooking.setItemId(item.getId());
        futureBooking.setBookingApproved("FUTURE");
        futureBooking.setBooker(itemBooker);
        futureBooking.setStart(LocalDateTime.now().plusYears(1));
        futureBooking.setEnd(LocalDateTime.now().plusYears(2));

        bookingService.createBooking(itemBooker.getId(), futureBooking);


        Assertions.assertEquals("FUTURE",
                bookingService.getBooking(futureBooking.getId(), itemBooker.getId()).getStatus());

    }

}

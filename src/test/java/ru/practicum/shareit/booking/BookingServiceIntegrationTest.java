package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
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

    @Test
    public void should_returnBookingList() {

        User itemOwner = new User();
        itemOwner.setName("Vitaly");
        itemOwner.setEmail("adse@sa.rn");

        User itemBooker = new User();
        itemBooker.setName("Бронировальщик");
        itemBooker.setEmail("booker@example.com");

        UserDto itemOwnerDto = userService.create(itemOwner);
        UserDto itemBookerDto = userService.create(itemBooker);

        Item item = new Item();
        item.setDescription("Дрель обычная");
        item.setName("Дрель");
        item.setAvailable(true);

        ItemDto itemDto = itemService.create(item, itemOwnerDto.getId());

        Booking futureBooking = new Booking();
        futureBooking.setItemId(itemDto.getId());
        futureBooking.setBookingApproved("FUTURE");
        futureBooking.setBookerId(itemBookerDto.getId());
        futureBooking.setStart(LocalDateTime.now().plusYears(1));
        futureBooking.setEnd(LocalDateTime.now().plusYears(2));

        bookingService.createBooking(itemBookerDto.getId(), futureBooking);


        Assertions.assertEquals("FUTURE",
                bookingService.getBooking(futureBooking.getId(), itemBookerDto.getId()).getStatus());

    }

}

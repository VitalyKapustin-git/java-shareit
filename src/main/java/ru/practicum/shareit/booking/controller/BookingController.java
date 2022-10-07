package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.core.exceptions.BadRequestException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService bookingService;

    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    /*TODO Проверка при создании, существует ли пользователь
      TODO Проверка при создании, cуществует ли item
      TODO Проверка при создании, что конец брони не раньше старта
      TODO Проверка при создании, что конец брони не в прошлом
      TODO Проверка при создании, что старт брони не в прошлом

      TODO Проверка при запросе брони, что существует (404)
      TODO Проверка при запросе брони по id на создателя брони и запрашивающего (должен быть один и тот же)

     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getAllBookings(state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                        @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getOwnerBookings(state, bookerId);
    }

    @PostMapping
    public Booking createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId, @RequestBody Booking booking) {

        System.out.printf(booking.getStart() + "       " + booking.getEnd());

        return bookingService.createBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestParam String approved,
                                 @PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.setApprove(Boolean.parseBoolean(approved), bookingId, bookerId);
    }

    @GetMapping("/abc/{itemId}")
    public List<Booking> getAllBookingsForItem(@PathVariable long itemId) {
        return bookingService.getAllBookingsForItem(itemId);
    }


}

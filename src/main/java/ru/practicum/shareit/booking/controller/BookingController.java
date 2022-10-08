package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

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

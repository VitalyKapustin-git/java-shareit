package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

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
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId, @Valid @RequestBody Booking booking) {
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

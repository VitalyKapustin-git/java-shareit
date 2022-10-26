package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getBooking(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                           @RequestHeader("X-Sharer-User-Id") long bookerId,
                                           @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return bookingService.getAllBookings(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return bookingService.getOwnerBookings(state, bookerId, from, size);
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

}

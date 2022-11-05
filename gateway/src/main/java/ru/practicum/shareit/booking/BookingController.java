package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, bookerId, from, size);
        return bookingClient.getAllBookings(state, bookerId, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return bookingClient.getOwnerBookings(state, bookerId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                @Valid @RequestBody Booking booking) {
        return bookingClient.createBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestParam String approved,
                                             @PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingClient.setApprove(Boolean.parseBoolean(approved), bookerId, bookingId);
    }

}

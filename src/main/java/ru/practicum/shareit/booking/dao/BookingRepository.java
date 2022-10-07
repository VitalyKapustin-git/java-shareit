package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking getBookingById(long id);

    List<Booking> getBookingsByItemId(long itemId);

    List<Booking> getBookingsByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> getBookingsByItemIdInOrderByStartDesc(List<Long> itemsId);

    @Query("select b from Booking b where ?1 between b.start and b.end and b.itemId = ?2")
    List<Booking> getCrossBookingsForItem(LocalDateTime dt, long itemId);

    // ---
    @Query("select b from Booking b where (current_timestamp between b.start and b.end) " +
            "and b.bookerId = ?1 order by b.start DESC")
    List<Booking> getCurrentBookings(long bookerId);

    @Query("select b from Booking b where current_timestamp > b.end and b.bookerId = ?1 order by b.start DESC")
    List<Booking> getPastBookings(long bookerId);

    @Query("select b from Booking b where current_timestamp < b.start and b.bookerId = ?1 order by b.start DESC")
    List<Booking> getFutureBookings(long bookerId);

    @Query("select b from Booking b where b.bookingApproved = 'WAITING' and b.bookerId = ?1 order by b.start DESC")
    List<Booking> getWaitingBookings(long bookerId);

    @Query("select b from Booking b where b.bookingApproved = 'REJECTED' and b.bookerId = ?1 order by b.start DESC")
    List<Booking> getRejectedBookings(long bookerId);
    // ---

    // ---
    @Query("select b from Booking b where (current_timestamp between b.start and b.end) " +
            "and b.itemId in ?1 order by b.start DESC")
    List<Booking> getCurrentOwnerBookings(List<Long> itemsId);

    @Query("select b from Booking b where current_timestamp > b.end and b.itemId in ?1 order by b.start DESC")
    List<Booking> getPastOwnerBookings(List<Long> itemsId);

    @Query("select b from Booking b where current_timestamp < b.start and b.itemId in ?1 order by b.start DESC")
    List<Booking> getFutureOwnerBookings(List<Long> itemsIdd);

    @Query("select b from Booking b where b.bookingApproved = 'WAITING' and b.itemId in ?1 order by b.start DESC")
    List<Booking> getWaitingOwnerBookings(List<Long> itemsId);

    @Query("select b from Booking b where b.bookingApproved = 'REJECTED' and b.itemId in ?1 order by b.start DESC")
    List<Booking> getRejectedOwnerBookings(List<Long> itemsId);
    // ---




//    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

}

package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking getBookingById(long id);

    List<Booking> getBookingsByItem_Id(long itemId);

    List<Booking> getBookingsByBooker_IdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> getBookingsByItem_IdInOrderByStartDesc(List<Long> itemsId, Pageable pageable);

    @Query("select b from Booking b where ?1 between b.start and b.end and b.item.id = ?2")
    List<Booking> getCrossBookingsForItem(LocalDateTime dt, long itemId);

    // ---
    @Query("select b from Booking b where (current_timestamp between b.start and b.end) " +
            "and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getCurrentBookings(long bookerId, Pageable pageable);

    @Query("select b from Booking b where current_timestamp > b.end and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getPastBookings(long bookerId, Pageable pageable);

    @Query("select b from Booking b where current_timestamp > b.end and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getPastBookings(long bookerId);

    @Query("select b from Booking b where current_timestamp < b.start and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getFutureBookings(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.bookingApproved = 'WAITING' and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getWaitingBookings(long bookerId, Pageable pageable);

    @Query("select b from Booking b where b.bookingApproved = 'REJECTED' and b.booker.id = ?1 order by b.start DESC")
    List<Booking> getRejectedBookings(long bookerId, Pageable pageable);
    // ---

    // ---
    @Query("select b from Booking b where (current_timestamp between b.start and b.end) " +
            "and b.item.id in ?1 order by b.start DESC")
    List<Booking> getCurrentOwnerBookings(List<Long> itemsId, Pageable pageable);

    @Query("select b from Booking b where current_timestamp > b.end and b.item.id in ?1 order by b.start DESC")
    List<Booking> getPastOwnerBookings(List<Long> itemsId, Pageable pageable);

    @Query("select b from Booking b where current_timestamp < b.start and b.item.id in ?1 order by b.start DESC")
    List<Booking> getFutureOwnerBookings(List<Long> itemsId, Pageable pageable);

    @Query("select b from Booking b where b.bookingApproved = 'WAITING' and b.item.id in ?1 order by b.start DESC")
    List<Booking> getWaitingOwnerBookings(List<Long> itemsId, Pageable pageable);

    @Query("select b from Booking b where b.bookingApproved = 'REJECTED' and b.item.id in ?1 order by b.start DESC")
    List<Booking> getRejectedOwnerBookings(List<Long> itemsId, Pageable pageable);
    // ---

}

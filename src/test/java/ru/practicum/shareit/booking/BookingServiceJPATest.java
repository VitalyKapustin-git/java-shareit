package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceJPATest {

    private final EntityManager em;

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

        em.persist(itemOwner);
        em.persist(itemBooker);
        item.setOwnerId(itemOwner.getId());
        em.persist(item);

    }

    @AfterAll
    public void afterEach() {

        em.clear();

    }

    @Test
    public void should_ReturnPastBookings() throws InterruptedException {

        Booking pastBooking = new Booking();
        pastBooking.setItemId(item.getId());
        pastBooking.setBookingApproved("PAST");
        pastBooking.setBookerId(itemBooker.getId());
        pastBooking.setStart(LocalDateTime.now().plusSeconds(2));
        pastBooking.setEnd(LocalDateTime.now().plusSeconds(3));

        em.persist(pastBooking);

        Thread.sleep(4000);

        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where :cur_date > b.end " +
                        "and b.bookerId = :booker_id order by b.start DESC", Booking.class);

        List<Booking> pastBookings = query
                .setParameter("cur_date", LocalDateTime.now())
                .setParameter("booker_id", itemBooker.getId())
                .getResultList();

        Assertions.assertEquals(pastBooking.getStart(), pastBookings.get(0).getStart());

    }

    @Test
    public void should_ReturnFutureBookings() {

        Booking futureBooking = new Booking();
        futureBooking.setItemId(item.getId());
        futureBooking.setBookingApproved("FUTURE");
        futureBooking.setBookerId(itemBooker.getId());
        futureBooking.setStart(LocalDateTime.now().plusYears(1));
        futureBooking.setEnd(LocalDateTime.now().plusYears(2));

        em.persist(futureBooking);

        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where :cur_date < b.end " +
                        "and b.bookerId = :booker_id order by b.start DESC", Booking.class);

        List<Booking> futureBookings = query
                .setParameter("cur_date", LocalDateTime.now())
                .setParameter("booker_id", itemBooker.getId())
                .getResultList();

        Assertions.assertEquals(futureBooking.getStart(), futureBookings.get(0).getStart());

    }

    @Test
    public void should_ReturnWaitingBookings() {

        Booking waitingBooking = new Booking();
        waitingBooking.setItemId(item.getId());
        waitingBooking.setBookerId(itemBooker.getId());
        waitingBooking.setStart(LocalDateTime.now().plusMonths(1));
        waitingBooking.setEnd(LocalDateTime.now().plusMonths(2));

        em.persist(waitingBooking);

        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where b.bookingApproved = 'WAITING' " +
                        "and b.bookerId = :booker_id order by b.start DESC", Booking.class);

        List<Booking> waitingBookings = query
                .setParameter("booker_id", itemBooker.getId())
                .getResultList();

        Assertions.assertEquals(waitingBooking.getStart(), waitingBookings.get(0).getStart());

    }


    @Test
    public void should_ReturnRejectedBookings() {

        Booking rejectedBooking = new Booking();
        rejectedBooking.setItemId(item.getId());
        rejectedBooking.setBookingApproved("REJECTED");
        rejectedBooking.setBookerId(itemBooker.getId());
        rejectedBooking.setStart(LocalDateTime.now().plusYears(2));
        rejectedBooking.setEnd(LocalDateTime.now().plusYears(3));

        em.persist(rejectedBooking);

        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where b.bookingApproved = 'REJECTED' " +
                        "and b.bookerId = :booker_id order by b.start DESC", Booking.class);

        List<Booking> rejectedBookings = query
                .setParameter("booker_id", itemBooker.getId())
                .getResultList();

        Assertions.assertEquals(rejectedBooking.getStart(), rejectedBookings.get(0).getStart());
    }

    @Test
    public void should_ReturnCurrentBookings() throws InterruptedException {

        Booking currentBooking = new Booking();
        currentBooking.setItemId(item.getId());
        currentBooking.setBookingApproved("CURRENT");
        currentBooking.setBookerId(itemBooker.getId());
        currentBooking.setStart(LocalDateTime.now().plusSeconds(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(5));

        em.persist(currentBooking);

        Thread.sleep(2000);

        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where (:cur_date between b.start and b.end) " +
                        "and b.bookerId = :booker_id order by b.start DESC", Booking.class);

        List<Booking> currentBookings = query
                .setParameter("cur_date", LocalDateTime.now())
                .setParameter("booker_id", itemBooker.getId())
                .getResultList();

        Assertions.assertEquals(currentBooking.getStart(), currentBookings.get(0).getStart());

    }

}




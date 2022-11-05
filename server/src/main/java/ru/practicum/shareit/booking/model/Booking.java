package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @Transient
    private long itemId;

    @ManyToOne(cascade = CascadeType.ALL)
    private User booker;

    @ManyToOne(cascade = CascadeType.ALL)
    private Item item;

    @Column(name = "approve_status")
    @JsonProperty(value = "status")
    private String bookingApproved = "WAITING";

}
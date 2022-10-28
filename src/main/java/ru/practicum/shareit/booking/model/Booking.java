package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date")
    @NotNull(message = "You must setup start date of booking")
    private LocalDateTime start;

    @Column(name = "end_date")
    @NotNull(message = "You must setup end date of booking")
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
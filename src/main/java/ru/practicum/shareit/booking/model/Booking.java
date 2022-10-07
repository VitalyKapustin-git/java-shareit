package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Getter
@Setter
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

    @Column(name = "item_id")
//    @JsonProperty("item.id")
    private long itemId;

    @Column(name = "booker_id")
//    @JsonProperty("booker.id")
    private long bookerId;

    @Column(name = "approve_status")
    @JsonProperty(value = "status")
    private String bookingApproved = "WAITING";

}
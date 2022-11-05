package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class Booking {

    @Id
    private long id;

    @FutureOrPresent
    @NotNull(message = "You must setup start date of booking")
    private LocalDateTime start;

    @Future
    @NotNull(message = "You must setup end date of booking")
    private LocalDateTime end;

    private long itemId;

    @JsonProperty(value = "status")
    private String bookingApproved = "WAITING";

}
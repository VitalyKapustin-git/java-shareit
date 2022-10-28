package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingItemDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private long itemId;

    private long bookerId;

    private String status;

}

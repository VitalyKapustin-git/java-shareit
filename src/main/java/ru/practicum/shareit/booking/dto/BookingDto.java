package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
public class BookingDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;

    private UserDto booker;

    private String status;

}

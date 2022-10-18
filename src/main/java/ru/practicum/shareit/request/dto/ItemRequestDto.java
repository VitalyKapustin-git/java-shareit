package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {

    Long id;

    String description;

    Long requestorId;

    LocalDateTime created;

    List<ItemDto> items;

}

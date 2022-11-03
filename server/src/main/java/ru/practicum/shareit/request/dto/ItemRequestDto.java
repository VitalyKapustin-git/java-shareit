package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {

    private long id;

    private String description;

    private Long requestorId;

    private UserDto requestorDto;

    private LocalDateTime created = LocalDateTime.now();

    private List<ItemDto> items;

}

package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private long id;

    @NotBlank(message = "Description couldn't be empty!")
    private String description;

    private Long requestorId;

    private UserDto requestorDto;

    private LocalDateTime created = LocalDateTime.now();

    private List<ItemDto> items;

}

package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemDto {

    long id;

    private String name;

    private String description;

    @JsonProperty("available")
    private Boolean available;

    private long ownerId;

    private Long requestId;

}
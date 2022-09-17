package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    @JsonProperty("available")
    private Boolean available;
//    private Long request;
}
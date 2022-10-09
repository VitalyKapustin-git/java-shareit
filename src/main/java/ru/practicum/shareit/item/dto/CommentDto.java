package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;

}

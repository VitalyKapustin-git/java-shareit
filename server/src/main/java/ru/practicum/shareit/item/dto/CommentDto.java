package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    private String itemName;

    private String authorName;

    private LocalDateTime created;

}

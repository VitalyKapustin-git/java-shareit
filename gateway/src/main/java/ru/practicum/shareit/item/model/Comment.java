package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    private long id;

    private String text;

    private long itemId;

    private long authorId;

    private LocalDateTime created = LocalDateTime.now();

}

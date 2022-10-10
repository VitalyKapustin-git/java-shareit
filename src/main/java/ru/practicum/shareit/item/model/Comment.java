package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String text;

    @Column(name = "item_id")
    private long itemId;

    @Column(name = "author_id")
    private long authorId;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

}

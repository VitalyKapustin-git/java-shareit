package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Column(name = "is_available")
    private Boolean available;

    @NotNull
    @Column(name = "owner_id")
    private long ownerId;

}

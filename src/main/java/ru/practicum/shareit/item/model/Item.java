package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    long ownerId;
    long requestId;
}

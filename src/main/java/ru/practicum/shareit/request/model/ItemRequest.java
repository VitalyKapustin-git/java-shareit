package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank
    String description;

    @Column(name = "requestor_id")
    Long requestorId;

    LocalDateTime created = LocalDateTime.now();

}

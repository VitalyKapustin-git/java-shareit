package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @NotBlank
    String name;

    @Email
    @NotBlank
    @Email(message = "Email пользователя (->xxx<-@mail.com) может включать латинские буквы (a-z), цифры (0-9) и точку (.).",
            regexp = "^[\\w\\d]+\\@[\\w]+\\.[a-z]+$")
    String email;

}

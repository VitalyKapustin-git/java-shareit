package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    long id;

    @NotBlank
    String name;

    @NotBlank
    @Email(message = "Email пользователя (->xxx<-@mail.com) может включать латинские буквы (a-z), цифры (0-9) и точку (.).",
            regexp = "^[\\w\\d]+\\@[\\w]+\\.[a-z]+$")
    String email;

}

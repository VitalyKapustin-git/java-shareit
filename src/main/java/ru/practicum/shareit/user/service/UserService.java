package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto get(long userId);

    List<UserDto> getAll();

    void remove(long userId);
}

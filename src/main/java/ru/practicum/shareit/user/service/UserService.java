package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(UserDto userDto);
    User update(long userId, UserDto userDto);
    User get(long userId);
    List<User> getAll();
    void remove(long userId);
}

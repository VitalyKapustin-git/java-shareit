package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable long userId) {
        return userService.get(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.remove(userId);
    }
}
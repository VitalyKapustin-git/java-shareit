package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Primary
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(UserDto userDto) {
        log.info("[USER_SERVICE] Creating user -> {}", userDto);

        return userRepository.create(userDto);
    }

    @Override
    public User update(long userId, UserDto userDto) {
        log.info("[USER_SERVICE] Trying to update user...");

        return userRepository.update(userId, userDto);
    }

    @Override
    public User get(long userId) {
        log.info("[USER_SERVICE] Get user via id = {}", userId);

        return userRepository.get(userId);
    }

    @Override
    public List<User> getAll() {
        log.info("[USER_SERVICE] Collect info about all users...");

        return userRepository.getAll();
    }

    @Override
    public void remove(long userId) {
        log.info("[USER_SERVICE] Removing user with id = {}", userId);

        userRepository.remove(userId);
    }
}

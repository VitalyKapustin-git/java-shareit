package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(User user) {

        log.info("[USER_SERVICE] Creating user -> {}", user);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("[USER_SERVICE] Trying to update user...");

        User oldUser = userRepository.getUserById(userId);

        System.out.println("BEFORE: " + oldUser);

        if (userDto.getEmail() != null) oldUser.setEmail(userDto.getEmail());
        if (userDto.getName() != null) oldUser.setName(userDto.getName());

        System.out.println("AFTER: " + oldUser);

        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto get(long userId) {
        log.info("[USER_SERVICE] Get user via id = {}", userId);
        if (userRepository.getUserById(userId) == null) throw new NotFoundException("userId: " + userId);

        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.info("[USER_SERVICE] Collect info about all users...");

        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void remove(long userId) {
        log.info("[USER_SERVICE] Removing user with id = {}", userId);

        userRepository.removeUserById(userId);
    }
}

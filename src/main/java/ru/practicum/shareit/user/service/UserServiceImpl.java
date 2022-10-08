package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public UserDto create(UserDto userDto) {

        log.info("[USER_SERVICE] Creating user -> {}", userDto);

        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("[USER_SERVICE] Trying to update user...");

        User oldUser = userRepository.getUserById(userId);

        if (userDto.getEmail() != null) oldUser.setEmail(userDto.getEmail());
        if (userDto.getName() != null) oldUser.setName(userDto.getName());

        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto get(long userId) {
        log.info("[USER_SERVICE] Get user via id = {}", userId);
        if (userRepository.getUserById(userId) == null) throw new NotFoundException("userId: " + userId);

        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

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

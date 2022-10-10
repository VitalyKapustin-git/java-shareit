package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EmailExistException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users;
    long id;

    InMemoryUserRepository() {
        this.id = 1;
        this.users = new HashMap<>();
    }

    @Override
    public User create(UserDto user) {
        String email = user.getEmail();
        if (users.values().stream().anyMatch(x -> x.getEmail().equals(email))) throw new EmailExistException(email);

        users.put(id, User.builder()
                .id(id)
                .name(user.getName())
                .email(user.getEmail())
                .build()
        );

        return users.get(id++);
    }

    @Override
    public User update(long userId, UserDto userDto) throws EmailExistException {
        User existingUser = users.get(userId);

        String name = userDto.getName();
        String email = userDto.getEmail();

        if (users.values().stream().anyMatch(x -> x.getEmail().equals(email))) {
            throw new EmailExistException(email);
        }

        if (name != null) existingUser.setName(name);
        if (email != null) existingUser.setEmail(email);

        return users.get(userId);
    }

    @Override
    public User get(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("user with id = " + userId);
        }

        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void remove(long userId) {
        users.remove(userId);
    }
}

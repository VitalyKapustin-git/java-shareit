package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testCreateUser() {

        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setName("testUser");

        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);

        Assertions.assertEquals(userService.create(user).getId(), user.getId());
        Assertions.assertEquals(userService.create(user).getEmail(), user.getEmail());

    }

    @Test
    public void testUpdateUser() {
        UserService userService = new UserServiceImpl(userRepository);

        User oldUser = new User();
        oldUser.setId(1);
        oldUser.setEmail("test@example.com");
        oldUser.setName("testUser");

        UserDto updatedUser = new UserDto();
        updatedUser.setId(1);
        updatedUser.setEmail("newtest@example.com");

        Mockito
                .when(userRepository.save(oldUser))
                .thenReturn(oldUser);

        Mockito
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(oldUser);

        Assertions.assertEquals(userService.update(1, updatedUser).getEmail(), updatedUser.getEmail());

    }

    @Test
    public void testThrowOnEmptyUserIdWhenGet() {
        UserService userService = new UserServiceImpl(userRepository);

        Mockito
                .when(userRepository.getUserById(Mockito.anyLong()))
                .thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () -> userService.get(42343L));
    }


}

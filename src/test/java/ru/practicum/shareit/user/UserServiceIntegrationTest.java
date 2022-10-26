package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceIntegrationTest {

    private final UserService userService;

    User user;

    UserDto userDto;

    @AfterAll
    public void afterAll() {

        userService.remove(user.getId());

    }

    @Test
    @Order(1)
    public void should_ReturnCreatedUser() {

        user = new User();
        user.setName("Vitaly");
        user.setEmail("email@example.com");

        userDto = userService.create(user);

        Assertions.assertEquals(user.getId(), userDto.getId());

    }

    @Test
    @Order(2)
    public void should_UpdateUser() {

        UserDto newUserDto = new UserDto();
        newUserDto.setEmail("alloha@example.com");

        userService.update(user.getId(), newUserDto);

        Assertions.assertEquals(newUserDto.getEmail(), userService.get(user.getId()).getEmail());

    }

}

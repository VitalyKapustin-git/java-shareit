package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceJPATest {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    User itemOwnerUser;

    Item item;

    @BeforeAll
    public void setUp() {

        itemOwnerUser = new User();
        itemOwnerUser.setName("Test owner");
        itemOwnerUser.setEmail("test@email.com");

        userRepository.save(itemOwnerUser);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Супер дрель");
        item.setAvailable(true);
        item.setOwnerId(itemOwnerUser.getId());

        itemRepository.save(item);

    }

    @AfterAll
    public void afterAll() {

        itemRepository.deleteAll();
        userRepository.deleteAll();

    }


    @Test
    public void should_ReturnArrayWithIdsOfOwnItems() {

        List<Long> usersOwnItems = itemRepository.getAllUserItemsId(itemOwnerUser.getId());

        Assertions.assertEquals(1, usersOwnItems.size());
        Assertions.assertTrue(usersOwnItems.contains(item.getId()));

    }

    @Test
    public void should_FindItemByTextInNameOrDescription() {

        List<Item> itemsByText = itemRepository.findByText("дрель", Pageable.unpaged());

        Assertions.assertEquals(item.getId(), itemsByText.get(0).getId());

    }

}

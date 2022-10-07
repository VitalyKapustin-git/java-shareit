package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@Primary
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    private final BookingRepository bookingRepository;

    @Autowired
    ItemServiceImpl(ItemRepository itemRepository, UserService userService, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto item, long userId) {
        log.info("[ITEM_SERVICE] Trying to create new item {}", item);
        // Проверка, существует ли пользователь
        userService.get(userId);
        item.setOwnerId(userId);

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(item)));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        log.info("[ITEM_SERVICE] Trying to update item with id {}", itemId);
        Item oldItem = itemRepository.getItemById(itemId);

        if (userId != oldItem.getOwnerId()) {
            throw new NotOwnerException("You are not an author of the item post!!!");
        }

        String newName = itemDto.getName();
        String newDesr = itemDto.getDescription();
        Boolean newAvail = itemDto.getAvailable();

        if(newName != null) oldItem.setName(newName);
        if(newDesr != null) oldItem.setDescription(newDesr);
        if(newAvail != null) oldItem.setAvailable(newAvail);

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemWithBookingDto get(long itemId, long userId) {
        log.info("[ITEM_SERVICE] Getting item with id {}", itemId);
        if(itemRepository.getItemById(itemId) == null) throw new NotFoundException("itemId: " + itemId);

        Booking lastBookingDate = null;
        Booking nextBookingDate = null;

        // Если пользователь - владелец вещи, то вывести информацию по брони
        if(userId == itemRepository.getItemById(itemId).getOwnerId()) {

            /*
                Берем все брони для вещи, фильтруем по дате окончания брони, чтобы выяснить какие
                бронирования уже в прошлом (для выяснения последней ближней).
                Находим максимальную дату в ряде дат, которые расположены в ПРОШЛОМ.
                В ряде дат из прошлого, максимальная дата - это такая дата, которая в переводе на EpochTime имеет
                наибольшее кол-во секунд. Она и будет самой ближней к настоящему моменту времени.
            */
            lastBookingDate = bookingRepository.getBookingsByItemId(itemId).stream()
                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                    .max((x1, x2) -> x1.getEnd().isBefore(x2.getEnd()) ? 1 : 0)
                    .orElse(null);

            /*
                Берем все брони для вещи, фильтруем по дате окончания брони, чтобы выяснить какие
                бронирования в будушем (для выяснения последней ближней).
                Находим минимальную дату в ряде дат, которые расположены в БУДУЩЕМ>.
                В ряде дат в будущем, максимальная дата - это такая дата, которая в переводе на EpochTime имеет
                наименьшее кол-во секунд. Она и будет самой ближней к настоящему моменту времени.
            */
            nextBookingDate = bookingRepository.getBookingsByItemId(itemId).stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .min((x1, x2) -> x1.getStart().isBefore(x2.getStart()) ? 1 : 0)
                    .orElse(null);
        }

        return ItemMapper.toItemBookingDto(itemRepository.getItemById(itemId), lastBookingDate, nextBookingDate);
    }

    @Override
    public List<ItemWithBookingDto> getAll(long userId) {
        log.info("[ITEM_SERVICE] Trying to get all items for userId {}", userId);

        List<ItemWithBookingDto> userItemsWithBooking = new ArrayList<>();
        List<Item> userItems = itemRepository.getItemsByOwnerId(userId);

        userItems.forEach(
                x -> userItemsWithBooking.add(get(x.getId(), userId))
        );

        return userItemsWithBooking;
    }

    @Transactional
    @Override
    public void remove(long itemId, long userId) {
        log.info("[ITEM_SERVICE] Trying to delete item with id {} where authorId is {}", itemId, userId);

        itemRepository.removeItemByIdAndOwnerId(itemId, userId);
    }

    @Override
    public List<ItemDto> findByText(String text) {
        log.info("[ITEM_SERVICE] Trying to find item with pattern {}", text);

        if (text.isEmpty()) {
            return List.of();
        }

        return itemRepository.findByText(text).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}

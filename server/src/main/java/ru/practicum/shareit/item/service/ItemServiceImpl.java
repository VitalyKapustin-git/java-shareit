package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.core.exceptions.BadRequestException;
import ru.practicum.shareit.core.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Item item, long userId) {

        log.info("[ITEM_SERVICE] Trying to create new item {}", item);
        // Проверка, существует ли пользователь
        userService.get(userId);
        item.setOwnerId(userId);

        return ItemMapper.toItemDto(itemRepository.save(item));

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

        if (newName != null) oldItem.setName(newName);
        if (newDesr != null) oldItem.setDescription(newDesr);
        if (newAvail != null) oldItem.setAvailable(newAvail);

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemWithBookingDto get(long itemId, long userId) {
        log.info("[ITEM_SERVICE] Getting item with id {}", itemId);
        if (itemRepository.getItemById(itemId) == null) throw new NotFoundException("itemId: " + itemId);

        Booking lastBookingDate = null;
        Booking nextBookingDate = null;

        // Если пользователь - владелец вещи, то вывести информацию по брони
        if (userId == itemRepository.getItemById(itemId).getOwnerId()) {

            /*
                Берем все брони для вещи, фильтруем по дате окончания брони, чтобы выяснить какие
                бронирования уже в прошлом (для выяснения последней ближней).
                Находим максимальную дату в ряде дат, которые расположены в ПРОШЛОМ.
                В ряде дат из прошлого, максимальная дата - это такая дата, которая в переводе на EpochTime имеет
                наибольшее кол-во секунд. Она и будет самой ближней к настоящему моменту времени.
            */
            lastBookingDate = bookingRepository.getBookingsByItem_Id(itemId).stream()
                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                    .min((x1, x2) -> x1.getEnd().isBefore(x2.getEnd()) ? 1 : 0)
                    .orElse(null);

            /*
                Берем все брони для вещи, фильтруем по дате окончания брони, чтобы выяснить какие
                бронирования в будушем (для выяснения последней ближней).
                Находим минимальную дату в ряде дат, которые расположены в БУДУЩЕМ>.
                В ряде дат в будущем, максимальная дата - это такая дата, которая в переводе на EpochTime имеет
                наименьшее кол-во секунд. Она и будет самой ближней к настоящему моменту времени.
            */
            nextBookingDate = bookingRepository.getBookingsByItem_Id(itemId).stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .min((x1, x2) -> x1.getStart().isBefore(x2.getStart()) ? 0 : 1)
                    .orElse(null);

        }

        List<CommentDto> commentDtos = commentRepository.getAllItemComments(itemId).stream()
                .map(v -> {

                    CommentDto commentDto = CommentMapper.toCommentDto(v);
                    commentDto.setItemName(itemRepository.getItemById(v.getItemId()).getName());
                    commentDto.setAuthorName(userService.get(v.getAuthorId()).getName());

                    return commentDto;

                }).collect(Collectors.toList());

        return ItemMapper.toItemBookingDto(itemRepository.getItemById(itemId),
                lastBookingDate,
                nextBookingDate,
                commentDtos
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemWithBookingDto> getAll(long userId, int from, int size) {
        log.info("[ITEM_SERVICE] Trying to get all items for userId {}", userId);

        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemWithBookingDto> userItemsWithBooking = new ArrayList<>();
        List<Item> userItems = itemRepository.getItemsByOwnerIdOrderById(userId, pageable);

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

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> findByText(String text, int from, int size) {
        log.info("[ITEM_SERVICE] Trying to find item with pattern {}", text);

        if (text.isEmpty()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(from / size, size);

        return itemRepository.findByText(text, pageable).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(long itemId, long userId, Comment comment) {

        if (comment.getText().isBlank()) throw new BadRequestException("comment couldn't be empty.");

        long bookingsNumber = bookingRepository.getPastBookings(userId).stream()
                .filter(x -> x.getItem().getId() == itemId)
                .count();

        if (bookingsNumber == 0) throw new BadRequestException("you must use booked item" +
                " for full time period before leave comment");

        comment.setItemId(itemId);
        comment.setAuthorId(userId);

        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        commentDto.setItemName(itemRepository.getItemById(itemId).getName());
        commentDto.setAuthorName(userService.get(userId).getName());

        return commentDto;
    }

}

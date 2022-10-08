package ru.practicum.shareit.item.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dao.UserRepository;

@Component
public class CommentMapper {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Autowired
    CommentMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemName(itemRepository.getItemById(comment.getItemId()).getName())
                .authorName(userRepository.getUserById(comment.getAuthorId()).getName())
                .created(comment.getCreated())
                .build();
    }

}

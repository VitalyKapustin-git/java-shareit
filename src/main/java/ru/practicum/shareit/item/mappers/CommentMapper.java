package ru.practicum.shareit.item.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dao.UserRepository;

@Component
@AllArgsConstructor
public class CommentMapper {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public CommentDto toCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemName(itemRepository.getItemById(comment.getItemId()).getName());
        commentDto.setAuthorName(userRepository.getUserById(comment.getAuthorId()).getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

}

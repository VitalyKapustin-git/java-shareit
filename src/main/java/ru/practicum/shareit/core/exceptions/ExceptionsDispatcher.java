package ru.practicum.shareit.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.NotOwnerException;
import ru.practicum.shareit.user.exceptions.EmailExistException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionsDispatcher {
    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> notOwner(NotOwnerException e) {
        return Map.of("FORBIDDEN: ", e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(BadRequestException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NotFoundException e) {
        return Map.of("Not found: ", e.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> emailConflict(EmailExistException e) {
        return Map.of("Already exists: ", e.getMessage());
    }
}
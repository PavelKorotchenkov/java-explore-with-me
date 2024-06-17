package ru.practicum.exception;

public class CommentUpdateNotByAuthorException extends RuntimeException {
    public CommentUpdateNotByAuthorException(String message) {
        super(message);
    }
}

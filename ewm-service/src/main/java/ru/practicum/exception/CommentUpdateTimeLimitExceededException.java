package ru.practicum.exception;

public class CommentUpdateTimeLimitExceededException extends RuntimeException {
    public CommentUpdateTimeLimitExceededException(String message) {
        super(message);
    }
}

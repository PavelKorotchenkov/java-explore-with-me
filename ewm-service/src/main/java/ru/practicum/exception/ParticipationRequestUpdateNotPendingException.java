package ru.practicum.exception;

public class ParticipationRequestUpdateNotPendingException extends RuntimeException {
    public ParticipationRequestUpdateNotPendingException(String message) {
        super(message);
    }
}

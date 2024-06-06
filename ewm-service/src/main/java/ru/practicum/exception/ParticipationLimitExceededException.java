package ru.practicum.exception;

public class ParticipationLimitExceededException extends RuntimeException {
    public ParticipationLimitExceededException(String message) {
        super(message);
    }
}

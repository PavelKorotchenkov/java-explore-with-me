package ru.practicum.exception;

public class EventDateViolationException extends RuntimeException {
	public EventDateViolationException(String message) {
		super(message);
	}
}

package ru.practicum.exception;

public class IncorrectDateException extends RuntimeException {
	public IncorrectDateException(String message) {
		super(message);
	}
}

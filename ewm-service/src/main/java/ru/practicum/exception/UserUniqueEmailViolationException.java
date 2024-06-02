package ru.practicum.exception;

public class UserUniqueEmailViolationException extends RuntimeException {
	public UserUniqueEmailViolationException(String message) {
		super(message);
	}
}

package ru.practicum.exception;

public class CategoryUniqueNameViolationException extends RuntimeException {
	public CategoryUniqueNameViolationException(String message) {
		super(message);
	}
}

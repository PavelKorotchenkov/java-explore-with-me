package ru.practicum.exception;

public class ParticipationDuplicateRequestException extends RuntimeException {
	public ParticipationDuplicateRequestException(String message) {
		super(message);
	}
}

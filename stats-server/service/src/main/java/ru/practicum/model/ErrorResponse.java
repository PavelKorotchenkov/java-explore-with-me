package ru.practicum.model;

public class ErrorResponse {
	private String error;
	private String stackTrace;

	public ErrorResponse(String error) {
		this.error = error;
	}

	public ErrorResponse(String error, String stackTrace) {
		this.error = error;
		this.stackTrace = stackTrace;
	}

	public String getError() {
		return error;
	}

	public String getStackTrace() {
		return stackTrace;
	}

}
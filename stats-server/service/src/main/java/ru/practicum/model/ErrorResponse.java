package ru.practicum.model;

import java.util.List;

public class ErrorResponse {
	private String error;
	private List<String> stackTraceList;

	public ErrorResponse(String error) {
		this.error = error;
	}

	public ErrorResponse(String error, List<String> stackTrace) {
		this.error = error;
		this.stackTraceList = stackTrace;
	}

	public String getError() {
		return error;
	}

	public List<String> getStackTraceList() {
		return stackTraceList;
	}

}
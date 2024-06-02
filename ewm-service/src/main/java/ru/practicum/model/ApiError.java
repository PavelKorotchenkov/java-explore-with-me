package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
	private List<String> errors;
	private String message;
	private String reason;
	private String status;
	private String timestamp;
}

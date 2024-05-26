package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.model.ErrorResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice()
public class ErrorHandler {
	@ExceptionHandler()
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(final IncorrectDateException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler()
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(final MissingServletRequestParameterException e) {
		String message = String.format("Required request parameter '%s' is not present.",
				e.getParameterName());
		return new ErrorResponse(message);
	}

	@ExceptionHandler()
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handler(final Exception e) {
		log.error("Unexpected error: ", e);
		List<String> stackTraceList = Arrays.stream(e.getStackTrace())
				.map(StackTraceElement::toString)
				.collect(Collectors.toList());

		return new ErrorResponse(e.getMessage(), stackTraceList);
	}
}

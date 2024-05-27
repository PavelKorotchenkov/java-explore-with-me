package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class NewEventDto {
	@Max(2000)
	@Min(20)
	private String annotation;

	private Long category;

	@Max(7000)
	@Min(20)
	private String description;

	private String eventDate;

	private LocationDto location;

	private boolean paid;

	private int participantLimit;

	private boolean requestModeration;

	@Max(120)
	@Min(3)
	private String title;
}

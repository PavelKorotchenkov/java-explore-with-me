package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.location.LocationDto;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class UpdateEventUserRequest {
	@Size(min = 20, max = 2000)
	private String annotation;
	private Long category;
	@Size(min = 20, max = 7000)
	private String description;
	private String eventDate;
	private LocationDto location;
	private Boolean paid;
	@PositiveOrZero
	private Integer participantLimit;
	private Boolean requestModeration;
	private String stateAction;
	@Size(min = 3, max = 120)
	private String title;
}

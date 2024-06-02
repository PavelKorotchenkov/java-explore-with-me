package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.location.LocationDto;

import javax.validation.constraints.*;

@Data
public class NewEventDto {
	@Size(min = 20, max = 2000)
	@NotEmpty
	@NotBlank
	private String annotation;
	@NotNull
	private Long category;
	@Size(min = 20, max = 7000)
	@NotEmpty
	@NotBlank
	private String description;
	@NotEmpty
	@NotBlank
	private String eventDate;
	@NotNull
	private LocationDto location;
	private boolean paid = false;
	@PositiveOrZero
	private int participantLimit = 0;
	private boolean requestModeration = true;
	@Size(min = 3, max = 120)
	@NotEmpty
	@NotBlank
	private String title;
}

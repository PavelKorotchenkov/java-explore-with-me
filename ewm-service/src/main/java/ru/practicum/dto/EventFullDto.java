package ru.practicum.dto;

import lombok.Data;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Location;

@Data
public class EventFullDto {
	private String annotation;
	private CategoryDto category;
	private long confirmedRequests;
	private String createdOn;
	private String description;
	private String eventDate;
	private long id;
	private UserShortDto userShortDto;
	private Location location;
	private boolean paid;
	private int participantLimit;
	private String publishedOn;
	private boolean requestModeration;
	private String state;
	private String title;
	private long views;
}

package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

@Data
public class EventShortDto {
	private long id;
	private String description;
	private String annotation;
	private CategoryDto category;
	private long confirmedRequests;
	private String eventDate;
	private UserShortDto initiator;
	private boolean paid;
	private String title;
	private long views;
}

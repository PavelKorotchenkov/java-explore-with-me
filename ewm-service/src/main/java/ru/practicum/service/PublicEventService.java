package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSort;

import java.util.List;

public interface PublicEventService {
	List<EventShortDto> getPublicEvents(String text,
										List<Long> categories,
										Boolean paid,
										String rangeStart,
										String rangeEnd,
										boolean onlyAvailable,
										EventSort sort,
										int from,
										int size);

	EventFullDto getEventById(long id);
}

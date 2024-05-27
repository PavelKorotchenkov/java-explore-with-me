package ru.practicum.service;

import org.springframework.data.domain.Page;
import ru.practicum.dto.EventShortDto;

import java.util.List;

public interface PublicEventService {
	Page<EventShortDto> getPublicEvents(String text,
										List<Integer> categories,
										boolean paid,
										String rangeStart,
										String rangeEnd,
										boolean onlyAvailable,
										String sort,
										int from,
										int size);

}

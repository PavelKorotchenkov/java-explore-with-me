package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {

	List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories,
									String rangeStart, String rangeEnd, Pageable pageable);

	EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateRequest);
}

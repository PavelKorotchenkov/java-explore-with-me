package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
	List<EventShortDto> getPrivateEvents(long userId, Pageable pageable);

	EventFullDto postNewEvent(long userId, NewEventDto newEventDto);

	EventFullDto getPrivateEventFullInfo(long userId, long eventId);

	EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

	List<ParticipationRequestDto> getPrivateEventParticipationRequests(long userId, long eventId);

	EventRequestStatusUpdateResult updateParticipationRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);
}

package ru.practicum.service;

import ru.practicum.dto.NewEventDto;

public interface PrivateEventService {
	void postNewEvent(long userId, NewEventDto newEventDto);
}

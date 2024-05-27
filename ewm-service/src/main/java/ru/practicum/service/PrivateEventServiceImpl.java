package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.mapper.EventDtoMapper;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

	private final EventRepository eventRepository;

	@Override
	public void postNewEvent(long userId, NewEventDto newEventDto) {
		Event event = EventDtoMapper.INSTANCE.newEventDtoToEvent(newEventDto);
		//User initiator =
		eventRepository.save(event);
	}
}

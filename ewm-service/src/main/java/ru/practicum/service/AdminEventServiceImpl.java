package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.location.LocationDtoMapper;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.exception.EventUpdateException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.util.LocalDateTimeStringParser;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
	private final LocationRepository locationRepository;
	private final EventRepository eventRepository;
	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	@Override
	public List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories,
										   String rangeStart, String rangeEnd, Pageable pageable) {
		List<EventState> validStates = validateEventStates(states);
		LocalDateTime startDate = getStartDate(rangeStart);
		LocalDateTime endDate = getEndDate(rangeEnd);

		Page<Event> events;
		if (endDate == null) {
			events = eventRepository
					.findAllEventsWithoutEndDateForAdmin(users, validStates, categories, startDate, pageable);
		} else {
			events = eventRepository
					.findAllEventsForAdmin(users, validStates, categories, startDate, endDate, pageable);
		}

		return events.getContent().stream().map(EventDtoMapper.INSTANCE::eventToEventFullDto)
				.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateRequest) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
		applyEventUpdate(event, updateRequest);
		return EventDtoMapper.INSTANCE.eventToEventFullDto(eventRepository.save(event));
	}

	private void applyEventUpdate(Event event, UpdateEventAdminRequest updateRequest) {
		if (updateRequest.getEventDate() != null) {
			LocalDateTime eventUpdatedDate = LocalDateTimeStringParser.parseStringToLocalDateTime(updateRequest.getEventDate());

			if (eventUpdatedDate.isBefore(LocalDateTime.now())) {
				throw new IllegalArgumentException("The event date can't be in the past");
			}

			if (eventUpdatedDate.isBefore(LocalDateTime.now().plusHours(1))) {
				throw new IllegalArgumentException("The event date must be at least one hour before the publication date");
			}
			event.setEventDate(eventUpdatedDate);
		}

		if (updateRequest.getStateAction() != null) {
			EventStateAction updatedState = EventStateAction.getState(updateRequest.getStateAction())
					.orElseThrow(() -> new IllegalArgumentException("StateAction='" + updateRequest.getStateAction() + "' is not valid"));

			if (event.getState().equals(EventState.PUBLISHED) && (updatedState.equals(EventStateAction.PUBLISH_EVENT)
					|| updatedState.equals(EventStateAction.REJECT_EVENT))) {
				throw new EventUpdateException("Attempt to publish or reject the event failed because it is already published");
			}

			if (!event.getState().equals(EventState.PENDING) && updatedState.equals(EventStateAction.PUBLISH_EVENT)) {
				throw new EventUpdateException("Attempt to publish the event failed because it can be published only if it's in pending state");
			}

			if (updatedState.equals(EventStateAction.PUBLISH_EVENT)) {
				event.setState(EventState.PUBLISHED);
				event.setPublishedOn(LocalDateTime.now());
			} else if (updatedState.equals(EventStateAction.REJECT_EVENT)) {
				event.setState(EventState.CANCELED);
			}
		}

		if (updateRequest.getAnnotation() != null) {
			event.setAnnotation(updateRequest.getAnnotation());
		}

		if (updateRequest.getCategory() != null) {
			Category category = categoryRepository.findById(updateRequest.getCategory())
					.orElseThrow(() -> new EventUpdateException("Category name='" + updateRequest.getCategory() + "' does not exist"));
			event.setCategory(category);
		}

		if (updateRequest.getDescription() != null) {
			event.setDescription(updateRequest.getDescription());
		}

		if (updateRequest.getLocation() != null) {
			Location location = LocationDtoMapper.INSTANCE.locationDtoToLocation(updateRequest.getLocation());
			locationRepository.save(location);
			event.setLocation(location);
		}

		if (updateRequest.getPaid() != null) {
			event.setPaid(updateRequest.getPaid());
		}

		if (updateRequest.getParticipantLimit() != null) {
			event.setParticipantLimit(updateRequest.getParticipantLimit());
		}

		if (updateRequest.getRequestModeration() != null) {
			event.setRequestModeration(updateRequest.getRequestModeration());
		}

		if (updateRequest.getTitle() != null) {
			event.setTitle(updateRequest.getTitle());
		}
	}

	private static List<EventState> validateEventStates(List<String> states) {
		List<EventState> validStates = null;
		if (states != null && !states.isEmpty()) {
			validStates = states.stream()
					.map(s -> EventState.getState(s)
							.orElseThrow(() -> new IllegalArgumentException("EventState='" + s + "' is not valid")))
					.collect(Collectors.toList());
		}
		return validStates;
	}

	private LocalDateTime getStartDate(String rangeStart) {
		return (rangeStart != null && !rangeStart.isEmpty())
				? LocalDateTimeStringParser.parseStringToLocalDateTime(rangeStart)
				: LocalDateTime.now();
	}

	private LocalDateTime getEndDate(String rangeEnd) {
		return (rangeEnd != null && !rangeEnd.isEmpty())
				? LocalDateTimeStringParser.parseStringToLocalDateTime(rangeEnd)
				: null;
	}
}

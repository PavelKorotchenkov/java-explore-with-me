package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDtoMapper;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.dto.participation.ParticipationRequestMapper;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.EventUpdateException;
import ru.practicum.exception.ParticipationLimitExceededException;
import ru.practicum.exception.ParticipationRequestUpdateNotPendingException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.util.LocalDateTimeStringParser;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final LocationRepository locationRepository;
	private final CategoryRepository categoryRepository;
	private final ParticipationRequestRepository participationRequestRepository;

	@Transactional(readOnly = true)
	@Override
	public List<EventShortDto> getPrivateEvents(long initiatorId, Pageable pageable) {
		return eventRepository.findEventsByInitiatorId(initiatorId, pageable)
				.map(EventDtoMapper.INSTANCE::eventToEventShortDto)
				.getContent();
	}

	@Transactional
	@Override
	public EventFullDto postNewEvent(long userId, NewEventDto newEventDto) {
		LocalDateTime eventDate = LocalDateTimeStringParser.parseStringToLocalDateTime(newEventDto.getEventDate());
		validateEventDate(eventDate);

		Event event = EventDtoMapper.INSTANCE.newEventDtoToEvent(newEventDto);
		User initiator = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));
		Category category = categoryRepository.findById(newEventDto.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category with id='" + newEventDto.getCategory() + "' not found"));

		Location location = locationRepository.save(LocationDtoMapper.INSTANCE.locationDtoToLocation(newEventDto.getLocation()));

		event.setInitiator(initiator);
		event.setCategory(category);
		event.setLocation(location);
		event.setCreatedOn(LocalDateTime.now());
		event.setState(EventState.PENDING);
		event.setConfirmedRequests(0L);
		Event savedEvent = eventRepository.save(event);
		return EventDtoMapper.INSTANCE.eventToEventFullDto(savedEvent);
	}

	@Transactional(readOnly = true)
	@Override
	public EventFullDto getPrivateEventFullInfo(long userId, long eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));

		checkUserIsInitiator(event, userId);
		return EventDtoMapper.INSTANCE.eventToEventFullDto(event);
	}

	@Transactional
	@Override
	public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
		checkUserIsInitiator(event, userId);

		if (!event.getState().equals(EventState.CANCELED) && !event.getState().equals(EventState.PENDING)) {
			throw new EventUpdateException("Only pending or canceled events can be changed");
		}

		applyInitiatorChanges(event, updateEventUserRequest);
		return EventDtoMapper.INSTANCE.eventToEventFullDto(eventRepository.save(event));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ParticipationRequestDto> getPrivateEventParticipationRequests(long userId, long eventId) {
		List<ParticipationRequest> requests = participationRequestRepository.findByEventIdAndRequesterIdNot(eventId, userId);

		return requests.stream().map(ParticipationRequestMapper.INSTANCE::toDto).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public EventRequestStatusUpdateResult updateParticipationRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
		checkUserIsInitiator(event, userId);

		//проверяем, что нам передали валидный статус (подтверждено/отменено) и запоминаем его
		ParticipationRequestStatus status = ParticipationRequestStatus.getStatus(request.getStatus())
				.orElseThrow(() -> new IllegalArgumentException("Participation request status='" + request.getStatus() + "' is not valid"));

		//достаем все запросы на участие
		List<ParticipationRequest> requestsListToUpdate = participationRequestRepository.findAllById(request.getRequestIds());
		List<ParticipationRequest> confirmedRequests = new ArrayList<>();
		List<ParticipationRequest> rejectedRequests = new ArrayList<>();

		//проверяем что статус у запросов pending
		requestsListToUpdate.forEach(participationRequest -> {
			if (!participationRequest.getStatus().equals(ParticipationRequestStatus.PENDING)) {
				throw new ParticipationRequestUpdateNotPendingException("Participation request status='" + participationRequest.getStatus() + "' must be pending");
			}
		});

		//если заявки одобрены
		if (status.equals(ParticipationRequestStatus.CONFIRMED)) {
			//если лимит не установлен или нет модерации, одобряем заявки
			if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
				for (ParticipationRequest participationRequest : requestsListToUpdate) {
					participationRequest.setStatus(status);
					confirmedRequests.add(participationRequest);
				}
			} else {
				long participantsCount = event.getConfirmedRequests();
				int participantsLimit = event.getParticipantLimit();
				checkEventHasFreeSlots(participantsCount, participantsLimit);

				// проходимся по каждой заявке
				for (ParticipationRequest participationRequest : requestsListToUpdate) {
					// если количество участников достигло лимита, отменяем заявку
					if (participantsCount >= participantsLimit) {
						participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
						rejectedRequests.add(participationRequest);
					} else {
						// иначе одобряем заявку и увеличиваем счетчик кол-во участников
						// и кол-во подтвержденных участников у события
						participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
						participantsCount++;
						event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
						confirmedRequests.add(participationRequest);
					}
				}
			}
		} else {
			//если заявки не одобрены
			for (ParticipationRequest participationRequest : requestsListToUpdate) {
				participationRequest.setStatus(status);
				rejectedRequests.add(participationRequest);
			}
		}

		//сохраняем заявки с новым статусом
		participationRequestRepository.saveAll(requestsListToUpdate);
		List<ParticipationRequestDto> confirmedDtos = confirmedRequests.stream()
				.map(ParticipationRequestMapper.INSTANCE::toDto)
				.collect(Collectors.toList());

		List<ParticipationRequestDto> rejectedDtos = rejectedRequests.stream()
				.map(ParticipationRequestMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
		EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
		result.setConfirmedRequests(confirmedDtos);
		result.setRejectedRequests(rejectedDtos);

		//обновляем евент и его подтвержденное кол-во участников
		eventRepository.save(event);
		return result;
	}

	private void checkEventHasFreeSlots(long participantsCount, long participantsLimit) {
		if (participantsCount >= participantsLimit) {
			throw new ParticipationLimitExceededException("Participant limit='" + participantsLimit + "' exceeded");
		}
	}

	private void applyInitiatorChanges(Event event, UpdateEventUserRequest updateEventUserRequest) {
		if (updateEventUserRequest.getAnnotation() != null) {
			event.setAnnotation(updateEventUserRequest.getAnnotation());
		}

		if (updateEventUserRequest.getCategory() != null) {
			long catId = updateEventUserRequest.getCategory();
			event.setCategory(categoryRepository.findById(catId)
					.orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found")));
		}

		if (updateEventUserRequest.getDescription() != null) {
			event.setDescription(updateEventUserRequest.getDescription());
		}

		if (updateEventUserRequest.getEventDate() != null) {
			LocalDateTime eventUpdatedDate = LocalDateTimeStringParser.parseStringToLocalDateTime(updateEventUserRequest.getEventDate());
			validateEventDate(eventUpdatedDate);
			event.setEventDate(eventUpdatedDate);
		}

		if (updateEventUserRequest.getLocation() != null) {
			Location location = LocationDtoMapper.INSTANCE.locationDtoToLocation(updateEventUserRequest.getLocation());
			event.setLocation(location);
		}

		if (updateEventUserRequest.getPaid() != null) {
			event.setPaid(updateEventUserRequest.getPaid());
		}

		if (updateEventUserRequest.getParticipantLimit() != null) {
			event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
		}

		if (updateEventUserRequest.getRequestModeration() != null) {
			event.setRequestModeration(updateEventUserRequest.getRequestModeration());
		}
		if (updateEventUserRequest.getTitle() != null) {
			event.setTitle(updateEventUserRequest.getTitle());
		}
		if (updateEventUserRequest.getStateAction() != null
				&& updateEventUserRequest.getStateAction().equals(EventStateAction.CANCEL_REVIEW.name())) {
			event.setState(EventState.CANCELED);
		} else {
			event.setState(EventState.PENDING);
		}
	}

	private void validateEventDate(LocalDateTime eventDate) {
		if (eventDate.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("The event date '" + eventDate + "' can't be in the past");
		}

		if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
			throw new IllegalArgumentException("The event date '" + eventDate + "' must be at least in two hours");
		}
	}

	private void checkUserIsInitiator(Event event, long userId) {
		if (!(event.getInitiator().getId() == userId)) {
			throw new EntityNotFoundException("Event with id='" + event.getId() + "' not found");
		}
	}
}

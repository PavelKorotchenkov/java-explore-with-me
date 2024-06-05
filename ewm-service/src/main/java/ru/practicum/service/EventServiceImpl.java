package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.Client;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.location.LocationDtoMapper;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.dto.participation.ParticipationRequestMapper;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventStateAction;
import ru.practicum.enums.ParticipationRequestStatus;
import ru.practicum.exception.EventUpdateException;
import ru.practicum.exception.ParticipationLimitExceededException;
import ru.practicum.exception.ParticipationRequestUpdateNotPendingException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.util.LocalDateTimeStringParser;
import ru.practicum.util.OffsetPageRequest;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.model.QParticipationRequest.participationRequest;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final UserRepository userRepository;
	private final LocationRepository locationRepository;
	private final EventRepository eventRepository;
	private final ParticipationRequestRepository participationRequestRepository;
	private final CategoryRepository categoryRepository;

	private final ParticipationRequestMapper participationRequestMapper;
	private final LocationDtoMapper locationDtoMapper;
	private final EventDtoMapper eventDtoMapper;

	private final Client client;

	@Transactional(readOnly = true)
	@Override

	public List<EventFullDto> getAllByAdmin(AdminGetEventParamsDto params) {
		Predicate predicate = buildPredicate(params);
		Pageable page = OffsetPageRequest.createPageRequest(params.getFrom(), params.getSize());

		List<Event> events = eventRepository.findAll(predicate, page).toList();
		Map<Long, Event> eventMap = createEventMap(events);
		Map<Long, Long> confirmedRequestsCountMap = getConfirmedRequestsCount(eventMap.keySet());

		List<EventFullDto> eventFullDtos = events.stream().map(eventDtoMapper::eventToEventFullDto)
				.collect(Collectors.toList());

		updateEventConfirmedRequestsFullDto(confirmedRequestsCountMap, eventFullDtos);
		return eventFullDtos;
	}

	@Transactional
	@Override
	public EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest updateRequest) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
		applyUpdateByAdmin(event, updateRequest);
		return eventDtoMapper.eventToEventFullDto(eventRepository.save(event));
	}

	@Transactional(readOnly = true)
	@Override
	public List<EventShortDto> getAllByInitiator(long initiatorId, Pageable pageable) {
		return eventRepository.findByInitiatorId(initiatorId, pageable)
				.map(eventDtoMapper::eventToEventShortDto)
				.getContent();
	}

	@Transactional
	@Override
	public EventFullDto postNewByInitiator(long userId, NewEventDto newEventDto) {
		LocalDateTime eventDate = LocalDateTimeStringParser.parseStringToLocalDateTime(newEventDto.getEventDate());
		validateNewDate(eventDate);

		Event event = eventDtoMapper.newEventDtoToEvent(newEventDto);
		User initiator = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User with id='" + userId + "' not found"));
		Category category = categoryRepository.findById(newEventDto.getCategory())
				.orElseThrow(() -> new EntityNotFoundException("Category with id='" + newEventDto.getCategory() + "' not found"));

		Location location = locationRepository.save(locationDtoMapper.locationDtoToLocation(newEventDto.getLocation()));

		event.setInitiator(initiator);
		event.setCategory(category);
		event.setLocation(location);
		event.setCreatedOn(LocalDateTime.now());
		event.setState(EventState.PENDING);
		Event savedEvent = eventRepository.save(event);
		return eventDtoMapper.eventToEventFullDto(savedEvent);
	}

	@Transactional(readOnly = true)
	@Override
	public EventFullDto getOneByInitiator(long userId, long eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));

		checkUserIsInitiator(event, userId);
		return eventDtoMapper.eventToEventFullDto(event);
	}

	@Transactional
	@Override
	public EventFullDto updateByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event with id='" + eventId + "' not found"));
		checkUserIsInitiator(event, userId);

		if (!event.getState().equals(EventState.CANCELED) && !event.getState().equals(EventState.PENDING)) {
			throw new EventUpdateException("Only pending or canceled events can be changed");
		}

		applyUpdateByInitiator(event, updateEventUserRequest);
		return eventDtoMapper.eventToEventFullDto(eventRepository.save(event));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ParticipationRequestDto> getRequests(long userId, long eventId) {
		List<ParticipationRequest> requests = participationRequestRepository.findByEventIdAndRequesterIdNot(eventId, userId);

		return requests.stream().map(participationRequestMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
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
				long participantsCount = participationRequestRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
				int participantsLimit = event.getParticipantLimit();
				checkEventHasFreeSlots(participantsCount, participantsLimit);

				// проходимся по каждой заявке
				for (ParticipationRequest participationRequest : requestsListToUpdate) {
					// если количество участников достигло лимита, отменяем заявку
					if (participantsCount >= participantsLimit) {
						participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
						rejectedRequests.add(participationRequest);
					} else {
						// иначе одобряем заявку
						participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
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
				.map(participationRequestMapper::toDto)
				.collect(Collectors.toList());

		List<ParticipationRequestDto> rejectedDtos = rejectedRequests.stream()
				.map(participationRequestMapper::toDto)
				.collect(Collectors.toList());
		EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
		result.setConfirmedRequests(confirmedDtos);
		result.setRejectedRequests(rejectedDtos);
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EventShortDto> getAllPublic(PublicGetEventParamsDto params) {
		Predicate predicate = buildPredicate(params);
		Pageable page = OffsetPageRequest.createPageRequest(params.getFrom(), params.getSize(), Sort.by(Sort.Direction.DESC, "EventDate"));

		EventSort sortType = getValidatedSort(params.getSort());

		List<EventShortDto> eventShortDtos = new ArrayList<>();
		if (sortType == null || sortType == EventSort.EVENT_DATE) {
			eventShortDtos = getEventsSortedByDate(predicate, page);
		} else if (sortType == EventSort.VIEWS) {
			eventShortDtos = getEventsSortedByViews(predicate, params);
		}

		return eventShortDtos;
	}

	@Transactional(readOnly = true)
	@Override
	public EventFullDto getByIdPublic(long id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Event with id=" + id + " not found"));
		if (!event.getState().equals(EventState.PUBLISHED)) {
			throw new EntityNotFoundException("Event with id=" + id + " not found");
		}

		EventFullDto result = eventDtoMapper.eventToEventFullDto(event);

		String uri = "/events/" + event.getId();
		List<String> uris = List.of(uri);
		List<StatsResponseDto> stats = getStats(event.getPublishedOn(), LocalDateTime.now(), uris, true);

		if (!stats.isEmpty()) {
			long hits = stats.get(0).getHits();
			result.setViews(hits);
		}

		return result;
	}

	private List<EventShortDto> getEventsSortedByViews(Predicate predicate, PublicGetEventParamsDto params) {
		List<Event> resultEvents = (List<Event>) eventRepository.findAll(predicate);
		LocalDateTime startDate = getStartDate(params.getRangeStart());

		Map<Long, Event> eventMap = createEventMap(resultEvents);
		Map<Long, Long> confirmedRequestsCountMap = getConfirmedRequestsCount(eventMap.keySet());

		List<StatsResponseDto> stats = getStatsForEvents(resultEvents, startDate);
		Map<String, Long> viewsMap = createViewsMap(stats);

		List<EventShortDto> eventShortDtos = convertEventsToDtos(resultEvents, viewsMap);
		updateEventConfirmedRequestsShortDto(confirmedRequestsCountMap, eventShortDtos);
		eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());

		return eventShortDtos;
	}

	private List<EventShortDto> getEventsSortedByDate(Predicate predicate, Pageable page) {
		Page<Event> resultEvents = eventRepository.findAll(predicate, page);
		LocalDateTime startDate = getStartDate(null);

		Map<Long, Event> eventMap = createEventMap(resultEvents.getContent());
		Map<Long, Long> confirmedRequestsCountMap = getConfirmedRequestsCount(eventMap.keySet());

		List<StatsResponseDto> stats = getStatsForEvents(resultEvents.getContent(), startDate);
		Map<String, Long> viewsMap = createViewsMap(stats);

		List<EventShortDto> eventShortDtos = convertEventsToDtos(resultEvents.getContent());
		updateEventConfirmedRequestsShortDto(confirmedRequestsCountMap, eventShortDtos);
		setEventDtoViews(eventShortDtos, viewsMap);

		return eventShortDtos;
	}

	private List<EventShortDto> convertEventsToDtos(List<Event> events, Map<String, Long> viewsMap) {
		return events.stream()
				.map(event -> {
					EventShortDto dto = eventDtoMapper.eventToEventShortDto(event);
					String eventUri = "/events/" + event.getId();
					long views = viewsMap.getOrDefault(eventUri, 0L);
					dto.setViews(views);
					return dto;
				})
				.collect(Collectors.toList());
	}

	private Map<Long, Event> createEventMap(List<Event> resultEvents) {
		return resultEvents.stream().collect(Collectors.toMap(Event::getId, Function.identity()));
	}


	private Map<Long, Long> getConfirmedRequestsCount(Set<Long> eventIds) {
		List<Object[]> results = participationRequestRepository.countParticipantsInAndStatus(eventIds, ParticipationRequestStatus.CONFIRMED);
		return results.stream()
				.collect(Collectors.toMap(
						result -> (Long) result[0], //eventId
						result -> (Long) result[1]  //confirmed requests count
				));
	}

	private EventSort getValidatedSort(String sort) {
		return Optional.ofNullable(sort)
				.filter(s -> !s.isEmpty())
				.flatMap(EventSort::getSort)
				.orElse(null);
	}

	private void updateEventConfirmedRequestsShortDto(Map<Long, Long> confirmedRequestsCountMap, List<EventShortDto> eventsDto) {
		for (EventShortDto event : eventsDto) {
			Long count = confirmedRequestsCountMap.get(event.getId());
			if (count != null) {
				event.setConfirmedRequests(count);
			}
		}
	}

	private void updateEventConfirmedRequestsFullDto(Map<Long, Long> confirmedRequestsCountMap, List<EventFullDto> eventsDto) {
		for (EventFullDto event : eventsDto) {
			Long count = confirmedRequestsCountMap.get(event.getId());
			if (count != null) {
				event.setConfirmedRequests(count);
			}
		}
	}


	private List<StatsResponseDto> getStatsForEvents(List<Event> resultEvents, LocalDateTime startDate) {
		List<String> uris = resultEvents.stream()
				.map(Event::getId)
				.map(id -> "/events/" + id)
				.collect(Collectors.toList());
		List<StatsResponseDto> stats = getStats(startDate, LocalDateTime.now(), uris, false);
		return stats;
	}

	private Map<String, Long> createViewsMap(List<StatsResponseDto> stats) {
		return stats.stream().collect(Collectors.toMap(StatsResponseDto::getUri, StatsResponseDto::getHits));
	}


	private List<EventShortDto> convertEventsToDtos(List<Event> resultEvents) {
		return resultEvents.stream()
				.map(eventDtoMapper::eventToEventShortDto)
				.collect(Collectors.toList());
	}

	private void setEventDtoViews(List<EventShortDto> eventShortDtos, Map<String, Long> viewsMap) {
		for (EventShortDto eventDto : eventShortDtos) {
			String eventUri = "/events/" + eventDto.getId();
			long views = viewsMap.getOrDefault(eventUri, 0L);
			eventDto.setViews(views);
		}
	}

	private Predicate buildPredicate(PublicGetEventParamsDto params) {
		QEvent event = QEvent.event;
		BooleanBuilder builder = new BooleanBuilder();

		String text = params.getText();

		if (text != null && !text.isEmpty()) {
			builder.and(event.annotation.containsIgnoreCase(text)
					.or(event.description.containsIgnoreCase(text)));
		}

		List<Long> categories = params.getCategories();

		if (categories != null && !categories.isEmpty()) {
			builder.and(event.category.id.in(categories));
		}

		Boolean paid = params.getPaid();

		if (paid != null) {
			builder.and(event.paid.eq(paid));
		}

		String rangeStart = params.getRangeStart();
		LocalDateTime startDate = null;
		if (rangeStart != null) {
			startDate = getStartDate(rangeStart);
			builder.and(event.eventDate.after(startDate));
		}

		String rangeEnd = params.getRangeEnd();
		LocalDateTime endDate = null;
		if (rangeEnd != null) {
			endDate = getEndDate(rangeEnd);
			builder.and(event.eventDate.before(endDate));
		}

		validateDate(endDate, startDate);

		boolean onlyAvailable = params.isOnlyAvailable();

		if (onlyAvailable) {
			builder.and(event.participantLimit.gt(
					JPAExpressions
							.selectFrom(participationRequest)
							.where(participationRequest.event.id.eq(event.id))
							.groupBy(participationRequest.event.id)
							.select(participationRequest.count())));
		}

		return builder;
	}

	private Predicate buildPredicate(AdminGetEventParamsDto params) {
		QEvent event = QEvent.event;
		BooleanBuilder builder = new BooleanBuilder();

		List<Long> users = params.getUsers();

		if (users != null && !users.isEmpty()) {
			builder.and(event.initiator.id.in(users));
		}

		List<String> states = params.getStates();

		if (states != null && !states.isEmpty()) {
			List<EventState> validStates = validateStates(states);
			builder.and(event.state.in(validStates));
		}

		List<Long> categories = params.getCategories();

		if (categories != null && !categories.isEmpty()) {
			builder.and(event.category.id.in(categories));
		}

		String rangeStart = params.getRangeStart();

		if (rangeStart != null) {
			LocalDateTime startDate = getStartDate(rangeStart);
			builder.and(event.eventDate.after(startDate));
		}

		String rangeEnd = params.getRangeEnd();

		if (rangeEnd != null) {
			LocalDateTime endDate = getEndDate(rangeEnd);
			builder.and(event.eventDate.before(endDate));
		}

		return builder;
	}

	private void applyUpdateByAdmin(Event event, UpdateEventAdminRequest updateRequest) {
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
			Location location = locationDtoMapper.locationDtoToLocation(updateRequest.getLocation());
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

	private void applyUpdateByInitiator(Event event, UpdateEventUserRequest updateEventUserRequest) {
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
			validateNewDate(eventUpdatedDate);
			event.setEventDate(eventUpdatedDate);
		}

		if (updateEventUserRequest.getLocation() != null) {
			Location location = locationDtoMapper.locationDtoToLocation(updateEventUserRequest.getLocation());
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

	private void checkEventHasFreeSlots(long participantsCount, long participantsLimit) {
		if (participantsCount >= participantsLimit) {
			throw new ParticipationLimitExceededException("Participant limit='" + participantsLimit + "' exceeded");
		}
	}

	private void checkUserIsInitiator(Event event, long userId) {
		if (!(event.getInitiator().getId() == userId)) {
			throw new EntityNotFoundException("Event with id='" + event.getId() + "' not found");
		}
	}

	private static List<EventState> validateStates(List<String> states) {
		List<EventState> validStates = null;
		if (states != null && !states.isEmpty()) {
			validStates = states.stream()
					.map(s -> EventState.getState(s)
							.orElseThrow(() -> new IllegalArgumentException("EventState='" + s + "' is not valid")))
					.collect(Collectors.toList());
		}
		return validStates;
	}

	private void validateNewDate(LocalDateTime eventDate) {
		if (eventDate.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("The event date '" + eventDate + "' can't be in the past");
		}

		if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
			throw new IllegalArgumentException("The event date '" + eventDate + "' must be at least in two hours");
		}
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

	private void validateDate(LocalDateTime endDate, LocalDateTime startDate) {
		if (endDate != null && endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date should be after start date.");
		}
	}

	private List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		ClientRequestDto requestDto = new ClientRequestDto(start, end, uris, unique);
		return client.getStats(requestDto);
	}


}

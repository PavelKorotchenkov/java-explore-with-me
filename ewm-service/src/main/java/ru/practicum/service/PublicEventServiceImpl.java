package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.Client;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.dto.event.EventDtoMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSort;
import ru.practicum.enums.EventState;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.LocalDateTimeStringParser;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

	private final EventRepository eventRepository;
	private final Client client;

	@Transactional(readOnly = true)
	@Override
	public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
											   String rangeEnd, boolean onlyAvailable, EventSort sortValue, int from, int size) {

		LocalDateTime startDate = getStartDate(rangeStart);
		LocalDateTime endDate = getEndDate(rangeEnd);
		validateDate(endDate, startDate);
		String normalizedText = normalizeText(text);

		List<Event> resultEvents;

		if (onlyAvailable) {
			if (endDate == null) {
				resultEvents = eventRepository.findAvailablePublicEventsWithoutEndDate(normalizedText, categories, paid, startDate, EventState.PUBLISHED);
			} else {
				resultEvents = eventRepository.findAvailablePublicEvents(normalizedText, categories, paid, startDate, endDate, EventState.PUBLISHED);
			}
		} else {
			if (endDate == null) {
				resultEvents = eventRepository.findAllPublicEventsWithoutEndDate(normalizedText, categories, paid, startDate, EventState.PUBLISHED);
			} else {
				resultEvents = eventRepository.findAllPublicEvents(normalizedText, categories, paid, startDate, endDate, EventState.PUBLISHED);
			}
		}

		List<String> uris = resultEvents.stream()
				.map(Event::getId)
				.map(id -> "/events/" + id)
				.collect(Collectors.toList());

		List<StatsResponseDto> stats = getStats(startDate, LocalDateTime.now(), uris, false);

		Map<String, Long> viewsMap = stats.stream()
				.collect(Collectors.toMap(StatsResponseDto::getUri, StatsResponseDto::getHits));

		List<EventShortDto> eventShortDtos = resultEvents.stream()
				.map(EventDtoMapper.INSTANCE::eventToEventShortDto).collect(Collectors.toList());

		eventShortDtos.forEach(event -> {
			String eventUri = "/events/" + event.getId();
			long views = viewsMap.getOrDefault(eventUri, 0L);
			event.setViews(views);
		});

		if (sortValue != null) {
			if (sortValue.equals(EventSort.VIEWS)) {
				eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
			}
		}

		int startPage = Math.min(from * size, eventShortDtos.size());
		int endPage = Math.min((from + 1) * size, eventShortDtos.size());
		return eventShortDtos.subList(startPage, endPage);
	}

	@Transactional(readOnly = true)
	@Override
	public EventFullDto getEventById(long id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Event with id=" + id + " not found"));
		if (!event.getState().equals(EventState.PUBLISHED)) {
			throw new EntityNotFoundException("Event with id=" + id + " not found");
		}

		EventFullDto result = EventDtoMapper.INSTANCE.eventToEventFullDto(event);

		String uri = "/events/" + event.getId();
		List<String> uris = List.of(uri);
		List<StatsResponseDto> stats = getStats(event.getPublishedOn(), LocalDateTime.now(), uris, true);

		if (!stats.isEmpty()) {
			long hits = stats.get(0).getHits();
			result.setViews(hits);
		}

		return result;
	}

	private String normalizeText(String text) {
		return text != null ? text.toLowerCase() : null;
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

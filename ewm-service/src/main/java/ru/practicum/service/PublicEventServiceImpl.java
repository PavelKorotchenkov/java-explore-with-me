package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.Client;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.OffsetPageRequest;
import ru.practicum.util.LocalDateTimeStringParser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

	private final Client client;
	private final EventRepository eventRepository;

	@Override
	public Page<EventShortDto> getPublicEvents(String text, List<Integer> categories, boolean paid, String rangeStart,
											   String rangeEnd, boolean onlyAvailable, String sortValue, int from, int size) {

		LocalDateTime start = LocalDateTimeStringParser.parseStringToLocalDateTime(rangeStart);
		LocalDateTime end = LocalDateTimeStringParser.parseStringToLocalDateTime(rangeEnd);

		Sort sort = Sort.by(Sort.Direction.DESC, "EventDate");

		//TODO сортировка по VIEWS
		/*if (sortValue.equals("VIEWS")) {
			sort = Sort.by(Sort.Direction.DESC, "EventDate");
		}*/

		PageRequest pageRequest = OffsetPageRequest.createPageRequest(from, size, sort);

		Page<EventShortDto> resultEvents = eventRepository.findPublicEvents(text, categories, paid, start, end, onlyAvailable, pageRequest);

		List<String> uris = resultEvents.stream()
				.map(EventShortDto::getId)
				.map(id -> "/events/" + id)
				.collect(Collectors.toList());

		List<StatsResponseDto> stats = getStats(start, end, uris, false);

		Map<String, Long> viewsMap = stats.stream()
				.collect(Collectors.toMap(StatsResponseDto::getUri, StatsResponseDto::getHits));

		resultEvents.forEach(event -> {
			String eventUri = "/events" + event.getId();
			long views = viewsMap.getOrDefault(eventUri, 0L);
			event.setViews(views);
		});

		return resultEvents;
	}

	private List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		//TODO передавать url через параметры окружения или через настройки приложения
		//TODO передавать клиента через autowired?
		Client client = new StatsClient("http://localhost:9090", new RestTemplateBuilder());
		ClientRequestDto requestDto = new ClientRequestDto(start, end, uris, unique);
		return client.getStats(requestDto);
	}
}

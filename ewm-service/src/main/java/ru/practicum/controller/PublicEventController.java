package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.Client;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.enums.EventSortingRequest;
import ru.practicum.service.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublicEventController {

	private final Client client;
	private final PublicEventService eventService;

	@GetMapping("/events")
	public Page<EventShortDto> getEvents(@RequestParam String text,
										 @RequestParam List<Integer> categories,
										 @RequestParam boolean paid,
										 @RequestParam String rangeStart,
										 @RequestParam String rangeEnd,
										 @RequestParam(defaultValue = "false") boolean onlyAvailable,
										 @RequestParam String sort,
										 @RequestParam(defaultValue = "0") int from,
										 @RequestParam(defaultValue = "10") int size,
										 HttpServletRequest request) {
		log.info("Request for list of EventShortDto with params: text: {}, categories: {}, paid: {}, rangeStart: {}, " +
						"rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}", text, categories, paid, rangeStart, rangeEnd,
				onlyAvailable, sort, from, size);
		EventSortingRequest validSort = EventSortingRequest.valueOf(sort);
		Page<EventShortDto> result = eventService
				.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, validSort.name(), from, size);
		log.info("Response for list of EventShortDto: found {} events", result.getSize());
		log.info("Saving stats for /events");
		saveStats(request);
		return result;
	}

	private void saveStats(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String ip = request.getRemoteAddr();
		client.saveStats(new StatsRequestDto("ewm-main-service", uri, ip, LocalDateTime.now()));
	}
}

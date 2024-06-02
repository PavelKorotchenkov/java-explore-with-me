package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.AdminEventService;
import ru.practicum.util.OffsetPageRequest;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

	private final AdminEventService eventService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
										@RequestParam(required = false) List<String> states,
										@RequestParam(required = false) List<Long> categories,
										@RequestParam(required = false) String rangeStart,
										@RequestParam(required = false) String rangeEnd,
										@RequestParam(defaultValue = "0") int from,
										@RequestParam(defaultValue = "10") int size) {
		log.info("Admin request for all the events: users: {}, states: {}, categories: {}, rangeStart: {}, " +
				"rangeEnd: {}, from: {}, size: {}", users, states, categories, rangeStart, rangeEnd, from, size);
		Pageable pageRequest = OffsetPageRequest.createPageRequest(from, size);
		List<EventFullDto> result = eventService.getAllEvents(users, states, categories, rangeStart, rangeEnd, pageRequest);
		log.info("Admin response for all the events: {}", result);
		return result;
	}

	@PatchMapping("/{eventId}")
	@ResponseStatus(HttpStatus.OK)
	public EventFullDto updateEvent(@PathVariable Long eventId,
									@RequestBody @Valid UpdateEventAdminRequest updateRequest) {
		log.info("Admin request for updating the event: eventId: {}, request: {}", eventId, updateRequest);
		EventFullDto result = eventService.updateEvent(eventId, updateRequest);
		log.info("Response for admin request for updating the event: eventId: {}, request: {}", eventId, result);
		return result;
	}
}

package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.service.ParticipationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@RestController
public class PrivateParticipationController {
	private final ParticipationService participationService;

	@GetMapping("/{userId}/requests")
	@ResponseStatus(HttpStatus.OK)
	public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
		log.info("Request for getting participation requests from user: {}", userId);
		List<ParticipationRequestDto> result = participationService.getRequests(userId);
		log.info("Response for getting participation requests, found {} requests", userId);
		return result;
	}

	@PostMapping("/{userId}/requests")
	@ResponseStatus(HttpStatus.CREATED)
	public ParticipationRequestDto createRequest(@PathVariable Long userId,
												 @RequestParam Long eventId) {
		log.info("Request for participating by user: {}, event: {}", userId, eventId);
		ParticipationRequestDto result = participationService.createRequest(userId, eventId);
		log.info("Response for participating: {}", result);
		return result;
	}

	@PatchMapping("/{userId}/requests/{requestId}/cancel")
	@ResponseStatus(HttpStatus.OK)
	public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
												 @PathVariable Long requestId) {
		log.info("Request for cancel participating by user: {}, request: {}", userId, requestId);
		ParticipationRequestDto result = participationService.cancelRequest(userId, requestId);
		log.info("Response for cancel participating: {}", result);
		return result;
	}
}

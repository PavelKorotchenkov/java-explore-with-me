package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.NewEventDto;

@Slf4j
@RestController("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {

	@PostMapping
	public void postNewEvent(@PathVariable Long userId,
						  @RequestBody NewEventDto newEventDto) {

	}

}

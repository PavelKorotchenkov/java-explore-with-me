package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.service.EventService;
import ru.practicum.util.OffsetPageRequest;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.info("Request for getting events initiated by: userId: {}, from: {}, size: {}", userId, from, size);
        Pageable pageRequest = OffsetPageRequest.createPageRequest(from, size);
        List<EventShortDto> result = eventService.getAllByInitiator(userId, pageRequest);
        log.info("Response for getting events: found {} events", result.size());
        return result;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postNewEvent(@PathVariable Long userId,
                                     @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Request for creating a new Event: userId: {}, NewEventDto: {}", userId, newEventDto);
        EventFullDto result = eventService.create(userId, newEventDto);
        log.info("Response for creating a new Event: EventFullDto: {}", result);
        return result;
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventFullInfo(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.info("Request for getting full info about event: {} by user: {}", eventId, userId);
        EventFullDto result = eventService.getOneByInitiator(userId, eventId);
        log.info("Response for getting full info about event: {}", result);
        return result;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Request for updating event: {} by user: {}, new event: {}", eventId, userId, updateEventUserRequest);
        EventFullDto result = eventService.updateByInitiator(userId, eventId, updateEventUserRequest);
        log.info("Response for updating event: {}", result);
        return result;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        log.info("Request for getting event participation requests for event: {} by user: {}", eventId, userId);
        List<ParticipationRequestDto> result = eventService.getRequests(userId, eventId);
        log.info("Response for getting event participation requests: {}", result);
        return result;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateParticipationRequestStatus(@PathVariable Long userId,
                                                                           @PathVariable Long eventId,
                                                                           @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Request for getting event participation requests for event: {} by user: {}, request: {}", eventId, userId, request);
        EventRequestStatusUpdateResult result = eventService.updateRequestStatus(userId, eventId, request);
        log.info("Response for getting event participation requests: {}", result);
        return result;
    }

}

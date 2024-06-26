package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.*;
import ru.practicum.dto.participation.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participation.EventRequestStatusUpdateResult;
import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAll(AdminGetEventParamsDto params);

    EventFullDto updateByAdmin(long eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> getAllByInitiator(long userId, Pageable pageable);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getOneByInitiator(long userId, long eventId);

    EventFullDto updateByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

    List<EventShortDto> getAll(PublicGetEventParamsDto params);

    EventFullDto getByIdPublic(long id);
}

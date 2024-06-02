package ru.practicum.service;

import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
	List<ParticipationRequestDto> getRequests(long userId);

	ParticipationRequestDto createRequest(long userId, long eventId);

	ParticipationRequestDto cancelRequest(long userId, long requestId);
}

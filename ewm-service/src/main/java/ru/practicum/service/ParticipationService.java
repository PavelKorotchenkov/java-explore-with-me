package ru.practicum.service;

import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
	List<ParticipationRequestDto> getAllByRequesterId(long userId);

	ParticipationRequestDto createNew(long userId, long eventId);

	ParticipationRequestDto cancel(long userId, long requestId);
}

package ru.practicum.service;

import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
	void saveStats(StatsDtoRequest request);

	List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

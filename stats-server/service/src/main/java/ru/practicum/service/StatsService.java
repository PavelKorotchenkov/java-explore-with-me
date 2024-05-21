package ru.practicum.service;

import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;

import java.util.List;

public interface StatsService {
	void saveStats(StatsDtoRequest request);

	List<StatsDtoResponse> getStats(String start, String end, List<String> uris, boolean unique);
}

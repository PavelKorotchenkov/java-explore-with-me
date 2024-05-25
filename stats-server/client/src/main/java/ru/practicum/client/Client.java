package ru.practicum.client;

import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.util.List;

public interface Client {
	void saveStats(StatsRequestDto requestDto);

	List<StatsResponseDto> getStats(ClientRequestDto requestDto);
}

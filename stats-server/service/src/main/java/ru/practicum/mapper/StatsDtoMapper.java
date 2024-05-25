package ru.practicum.mapper;

import ru.practicum.dto.StatsRequestDto;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;

public class StatsDtoMapper {
	public static Stats dtoToStats(StatsRequestDto dto) {
		Stats stats = new Stats();
		stats.setApp(dto.getApp());
		stats.setUri(dto.getUri());
		stats.setIp(dto.getIp());
		LocalDateTime dateTime = dto.getTimestamp();
		stats.setCreated(dateTime);

		return stats;
	}
}

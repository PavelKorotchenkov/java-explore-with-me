package ru.practicum.mapper;

import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsDtoMapper {
	public static Stats dtoToStats(StatsDtoRequest dto) {
		Stats stats = new Stats();
		stats.setApp(dto.getApp());
		stats.setUri(dto.getUri());
		stats.setIp(dto.getIp());
		LocalDateTime dateTime = LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		stats.setCreated(dateTime);

		return stats;
	}
}

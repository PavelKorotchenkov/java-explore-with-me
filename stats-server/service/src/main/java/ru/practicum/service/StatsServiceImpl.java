package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.IncorrectDateException;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.model.Stats;
import ru.practicum.repo.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

	private final StatsRepository repository;

	@Override
	public void saveStats(StatsDtoRequest request) {
		Stats stats = StatsDtoMapper.dtoToStats(request);
		repository.save(stats);
	}

	@Override
	public List<StatsDtoResponse> getStats(String start, String end, List<String> uris, boolean unique) {
		LocalDateTime startLocalDateTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endLocalDateTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (endLocalDateTime.isBefore(startLocalDateTime)) {
			log.info("Incorrect time interval for getStats: from {} to {}", start, end);
			throw new IncorrectDateException("End date should be after start date.");
		}

		if (unique) {
			return uris != null ? repository.findStatsUniqueIp(startLocalDateTime, endLocalDateTime, uris) :
					repository.findAllStatsUniqueIp(startLocalDateTime, endLocalDateTime);
		} else {
			return uris != null ? repository.findStatsAllIp(startLocalDateTime, endLocalDateTime, uris) :
					repository.findAllStatsAllIp(startLocalDateTime, endLocalDateTime);
		}
	}
}

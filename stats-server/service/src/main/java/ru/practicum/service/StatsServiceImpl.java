package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.model.Stats;
import ru.practicum.repo.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

	private final StatsRepository repository;

	@Transactional
	@Override
	public void saveStats(StatsRequestDto request) {
		Stats stats = StatsDtoMapper.dtoToStats(request);
		repository.save(stats);
	}

	@Override
	public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		if (unique) {
			return uris != null ? repository.findStatsUniqueIp(start, end, uris) :
					repository.findAllStatsUniqueIp(start, end);
		} else {
			return uris != null ? repository.findStatsAllIp(start, end, uris) :
					repository.findAllStatsAllIp(start, end);
		}
	}
}

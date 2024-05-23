package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.exception.IncorrectDateException;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

	private final StatsService statsService;

	@PostMapping("/hit")
	@ResponseStatus(HttpStatus.CREATED)
	public void saveStats(@RequestBody StatsDtoRequest request) {
		log.info("Saving statistics: {}", request);
		statsService.saveStats(request);
	}

	@GetMapping("/stats")
	@ResponseStatus(HttpStatus.OK)
	public List<StatsDtoResponse> getStats(@RequestParam String start,
										   @RequestParam String end,
										   @RequestParam(required = false) List<String> uris,
										   @RequestParam(defaultValue = "false") Boolean unique) {
		log.info("Getting statistics: from {} to {}, for uris {}, unique = {}", start, end, uris, unique);
		LocalDateTime startLocalDateTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endLocalDateTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (endLocalDateTime.isBefore(startLocalDateTime)) {
			log.info("Incorrect time interval for getStats: from {} to {}", start, end);
			throw new IncorrectDateException("End date should be after start date.");
		}
		List<StatsDtoResponse> stats = statsService.getStats(startLocalDateTime, endLocalDateTime, uris, unique);
		log.info("Statistics processing finished:  {}", stats);
		return stats;
	}
}

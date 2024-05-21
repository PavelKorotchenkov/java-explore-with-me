package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.service.StatsService;

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

	//TODO clean up log
	@GetMapping("/stats")
	@ResponseStatus(HttpStatus.OK)
	public List<StatsDtoResponse> getStats(@RequestParam String start,
										   @RequestParam String end,
										   @RequestParam(required = false) List<String> uris,
										   @RequestParam(defaultValue = "false") Boolean unique) {
		log.info("Getting statistics: from {} to {}, for uris {}, unique = {}", start, end, uris, unique);
		List<StatsDtoResponse> stats = statsService.getStats(start, end, uris, unique);
		log.info("Statistics processing finished:  {}", stats);
		return stats;
	}
}

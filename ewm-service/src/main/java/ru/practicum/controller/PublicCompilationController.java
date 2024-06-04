package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;
import ru.practicum.util.OffsetPageRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

	private final CompilationService compilationService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<CompilationDto> getCompilations(@RequestParam(required = false, name = "pinned") Boolean pinned,
												@RequestParam(defaultValue = "0") int from,
												@RequestParam(defaultValue = "10") int size) {
		log.info("Request for compilations: pinned: {}, from: {}, size: {}", pinned, from, size);
		Pageable page = OffsetPageRequest.createPageRequest(from, size);
		List<CompilationDto> result = compilationService.getAll(pinned, page);
		log.info("Response for compilations: result: {}", result);
		return result;
	}

	@GetMapping("/{compId}")
	@ResponseStatus(HttpStatus.OK)
	public CompilationDto getCompilation(@PathVariable Long compId) {
		log.info("Request for the compilation with id: {}", compId);
		CompilationDto result = compilationService.getById(compId);
		log.info("Response for the compilation: {}", result);
		return result;
	}
}

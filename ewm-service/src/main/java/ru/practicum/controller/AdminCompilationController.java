package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

	private final CompilationService adminCompilationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CompilationDto postNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
		log.info("Request for posting a new compilation: {}", newCompilationDto);
		CompilationDto response = adminCompilationService.postNew(newCompilationDto);
		log.info("Response for posting a new compilation: {}", response);
		return response;
	}

	@DeleteMapping("/{compId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCompilation(@PathVariable Long compId) {
		log.info("Request for deleting compilation with id: {}", compId);
		adminCompilationService.delete(compId);
	}

	@PatchMapping("/{compId}")
	@ResponseStatus(HttpStatus.OK)
	public CompilationDto updateCompilation(@PathVariable Long compId,
											@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
		log.info("Request for updating compilation with id: {}, updated compilation: {}", compId, updateCompilationRequest);
		CompilationDto response = adminCompilationService.update(compId, updateCompilationRequest);
		log.info("Response for updating compilation: {}", response);
		return response;
	}
}

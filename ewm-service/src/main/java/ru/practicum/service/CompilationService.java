package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
	CompilationDto postNew(NewCompilationDto compilationDto);

	void delete(long compId);

	CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest);

	List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

	CompilationDto getById(long compId);
}

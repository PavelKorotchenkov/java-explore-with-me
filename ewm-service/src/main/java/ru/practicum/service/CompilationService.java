package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
	CompilationDto postNewCompilation(NewCompilationDto compilationDto);

	void deleteCompilation(long compId);

	CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest);

	List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

	CompilationDto getCompilationById(long compId);
}

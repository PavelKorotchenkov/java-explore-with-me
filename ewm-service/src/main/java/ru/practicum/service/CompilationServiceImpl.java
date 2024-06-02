package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
	private final CompilationRepository compilationRepository;
	private final EventRepository eventRepository;

	@Override
	public CompilationDto postNewCompilation(NewCompilationDto compilationDto) {
		Compilation compilationToSave = CompilationDtoMapper.INSTANCE.newCompilationDtoToCompilation(compilationDto);
		return CompilationDtoMapper.INSTANCE.compilationToCompilationDto(compilationRepository.save(compilationToSave));
	}

	@Override
	public void deleteCompilation(long compId) {
		compilationRepository.deleteById(compId);
	}

	@Transactional
	@Override
	public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
		Compilation compilation = compilationRepository.findById(compId)
				.orElseThrow(() -> new EntityNotFoundException("Compilation with id='" + compId + "' not found"));

		if (updateCompilationRequest.getEvents() != null) {
			Set<Event> eventList = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
			compilation.setEvents(eventList);
		}

		if (updateCompilationRequest.getPinned() != null) {
			compilation.setPinned(updateCompilationRequest.getPinned());
		}

		if (updateCompilationRequest.getTitle() != null) {
			compilation.setTitle(updateCompilationRequest.getTitle());
		}

		return CompilationDtoMapper.INSTANCE.compilationToCompilationDto(compilationRepository.save(compilation));
	}

	@Transactional(readOnly = true)
	@Override
	public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
		Page<Compilation> compilationDtoPage;
		if (pinned == null) {
			compilationDtoPage = compilationRepository.findAll(pageable);
		} else {
			compilationDtoPage = compilationRepository.findByPinned(pinned, pageable);
		}

		return compilationDtoPage.getContent().stream()
				.map(CompilationDtoMapper.INSTANCE::compilationToCompilationDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public CompilationDto getCompilationById(long compId) {
		Compilation compilation = compilationRepository.findById(compId)
				.orElseThrow(() -> new EntityNotFoundException("Compilation with id='" + compId + "' not found"));
		return CompilationDtoMapper.INSTANCE.compilationToCompilationDto(compilation);
	}
}

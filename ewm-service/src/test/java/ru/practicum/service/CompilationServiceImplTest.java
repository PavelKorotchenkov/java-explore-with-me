package ru.practicum.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.DataUtils;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CompilationDtoMapper compilationDtoMapper;

    @InjectMocks
    private CompilationServiceImpl compilationServiceTest;

    @Test
    @DisplayName("Test create new compilation functionality")
    void givenNewCompilationDto_whenCreate_thenCompilationDtoIsReturned() {
        Compilation compilationTransient = DataUtils.getCompilationTransient();
        BDDMockito.given(compilationDtoMapper.newCompilationDtoToCompilation(any(NewCompilationDto.class))).willReturn(compilationTransient);
        CompilationDto compilationDto = DataUtils.getCompilationDto();
        BDDMockito.given(compilationDtoMapper.compilationToCompilationDto(any(Compilation.class))).willReturn(compilationDto);
        Compilation compilationPersisted = DataUtils.getCompilationPersisted();
        BDDMockito.given(compilationRepository.save(any(Compilation.class))).willReturn(compilationPersisted);
        CompilationDto savedCompilation = compilationServiceTest.create(DataUtils.getNewCompilationDto());

        assertThat(savedCompilation).isNotNull();
    }

    @Test
    @DisplayName("Test delete compilation by id functionality ")
    void givenCompilationId_whenDelete_thenRepositoryDeleteByIdMethodIsUsed() {
        compilationServiceTest.delete(1L);
        verify(compilationRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test update compilation by id functionality ")
    void givenCompilationIdAndNewCompilation_whenUpdate_thenCompilationDtoIsReturned() {
        Compilation compilationPersisted = DataUtils.getCompilationPersisted();
        BDDMockito.given(compilationRepository.findById(anyLong())).willReturn(Optional.of(compilationPersisted));
        BDDMockito.given(eventRepository.findByIdIn(anySet())).willReturn(new HashSet<>());
        BDDMockito.given(compilationDtoMapper.compilationToCompilationDto(any(Compilation.class))).willReturn(DataUtils.getCompilationDto());
        BDDMockito.given(compilationRepository.save(any(Compilation.class))).willReturn(compilationPersisted);

        CompilationDto savedCompilation = compilationServiceTest.update(1L, DataUtils.getUpdateCompilationRequest());

        assertThat(savedCompilation).isNotNull();
        verify(compilationRepository, times(1)).save(any(Compilation.class));
    }

    @Test
    @DisplayName("Test update compilation by id with incorrect id functionality ")
    void givenIncorrectCompilationId_whenUpdate_thenExceptionIsThrown() {
        BDDMockito.given(compilationRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> compilationServiceTest.update(1L, new UpdateCompilationRequest())
        );

        verify(compilationRepository, never()).save(any(Compilation.class));
    }

    @Test
    @DisplayName("Test get all compilations functionality")
    void givenUnpinned_whenGetAll_thenListOfCompilationsReturned() {
        Compilation compilation1 = DataUtils.getCompilationPersisted();
        Compilation compilation2 = DataUtils.getCompilationPersisted();
        compilation2.setId(2L);
        Page<Compilation> compilationPage = new PageImpl<>(List.of(compilation1, compilation2), Pageable.unpaged(), 2);
        BDDMockito.given(compilationRepository.findAll(Pageable.unpaged())).willReturn(compilationPage);
        BDDMockito.given(compilationDtoMapper.compilationToCompilationDto(any(Compilation.class))).willReturn(DataUtils.getCompilationDto());

        List<CompilationDto> result = compilationServiceTest.getAll(null, Pageable.unpaged());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test get compilation by id functionality")
    void givenCompilationId_whenGetById_thenCompilationIsReturned() {
        BDDMockito.given(compilationRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getCompilationPersisted()));
        BDDMockito.given(compilationDtoMapper.compilationToCompilationDto(any(Compilation.class))).willReturn(DataUtils.getCompilationDto());

        CompilationDto obtainedCompilation = compilationServiceTest.getById(1L);

        assertThat(obtainedCompilation).isNotNull();
    }
}
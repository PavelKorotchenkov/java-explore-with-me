package ru.practicum.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.Compilation;
import ru.practicum.util.DataUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CompilationRepositoryTest {

    @Autowired
    private CompilationRepository compilationRepository;

    @Test
    @DisplayName("Test find compilation by pinned false functionality")
    void givenPinnedFalseAndTrue_whenFindByPinnedFalse_thenNotPinnedCompilationIsReturned() {
        Compilation compilationNotPinned = DataUtils.getCompilationTransient();
        compilationRepository.save(compilationNotPinned);

        Compilation compilationPinned = DataUtils.getCompilationTransient();
        compilationPinned.setPinned(true);
        compilationRepository.save(compilationPinned);

        Page<Compilation> obtainedCompilation = compilationRepository.findByPinned(false, Pageable.unpaged());

        assertThat(obtainedCompilation).isNotNull();
        assertThat(obtainedCompilation.getContent().size()).isEqualTo(1);
        assertThat(obtainedCompilation.getContent().get(0).getPinned()).isFalse();
    }

    @Test
    @DisplayName("Test find compilation by pinned true functionality")
    void givenPinnedFalseAndTrue_whenFindByPinnedTrue_thenPinnedCompilationIsReturned() {
        Compilation compilationNotPinned = DataUtils.getCompilationTransient();
        compilationRepository.save(compilationNotPinned);

        Compilation compilationPinned = DataUtils.getCompilationTransient();
        compilationPinned.setPinned(true);
        compilationRepository.save(compilationPinned);

        Page<Compilation> obtainedCompilation = compilationRepository.findByPinned(true, Pageable.unpaged());

        assertThat(obtainedCompilation).isNotNull();
        assertThat(obtainedCompilation.getContent().size()).isEqualTo(1);
        assertThat(obtainedCompilation.getContent().get(0).getPinned()).isTrue();
    }
}
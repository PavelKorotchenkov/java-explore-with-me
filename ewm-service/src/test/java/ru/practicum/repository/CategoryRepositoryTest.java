package ru.practicum.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.model.Category;
import ru.practicum.util.DataUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenName_whenFindByName_thenCategoryIsReturned() {
        Category savedCategory = DataUtils.getCategoryTransient();
        categoryRepository.save(savedCategory);

        Category obtainedCategory = categoryRepository.findByName(savedCategory.getName()).orElse(null);
        assertThat(obtainedCategory).isNotNull();
        assertThat(obtainedCategory.getName()).isEqualTo(savedCategory.getName());
    }
}
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.CategoryInUseException;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.DataUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryDtoMapper categoryDtoMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private CategoryServiceImpl categoryServiceTest;

    @Test
    @DisplayName("Test create category functionality")
    void givenCategory_whenCreate_thenNewCategoryIsReturned() {
        NewCategoryDto categoryToSave = DataUtils.getNewCategoryDto();
        BDDMockito.given(categoryRepository.save(any(Category.class))).willReturn(DataUtils.getCategoryPersisted());
        BDDMockito.given(categoryDtoMapper.newCategoryDtoToCategory(any(NewCategoryDto.class))).willReturn(DataUtils.getCategoryTransient());
        BDDMockito.given(categoryDtoMapper.categoryToCategoryDto(any(Category.class))).willReturn(DataUtils.getCategoryDto());
        CategoryDto savedCategory = categoryServiceTest.create(categoryToSave);

        assertThat(savedCategory).isNotNull();
    }

    @Test
    @DisplayName("Test delete category functionality")
    void givenCatId_whenDeleteAndNoEvents_thenDeleteMethodIsUsed() {
        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(DataUtils.getCategoryPersisted()));
        BDDMockito.given(eventRepository.findFirstByCategoryId(1L)).willReturn(Optional.empty());

        categoryServiceTest.delete(1L);

        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete category with incorrect id functionality")
    void givenIncorrectCatId_whenDelete_thenExceptionIsThrown() {
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceTest.delete(anyLong())
        );

        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test delete category that is being used by event functionality")
    void givenCatIdAndEvent_whenDelete_thenExceptionIsThrown() {
        Category categoryPersisted = DataUtils.getCategoryPersisted();
        User user = DataUtils.getUserAuthorPersisted();
        Location location = DataUtils.getLocationPersisted();

        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(categoryPersisted));
        BDDMockito.given(eventRepository.findFirstByCategoryId(anyLong()))
                .willReturn(Optional.of(DataUtils.getEventPersisted(categoryPersisted, user, location)));

        assertThrows(
                CategoryInUseException.class,
                () -> categoryServiceTest.delete(anyLong())
        );

        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test update category functionality")
    void givenCatToUpdate_whenUpdate_thenRepositorySaveMethodIsCalled() {
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();
        Category categoryToUpdate = DataUtils.getCategoryTransient();
        CategoryDto updatedCategory = DataUtils.getCategoryDto();
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(categoryToUpdate));
        BDDMockito.given(categoryRepository.save(any(Category.class))).willReturn(categoryToUpdate);
        BDDMockito.given(categoryDtoMapper.categoryToCategoryDto(any(Category.class))).willReturn(updatedCategory);
        CategoryDto result = categoryServiceTest.update(newCategoryDto, 1L);

        assertThat(result).isNotNull();
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test update category with incorrect id functionality")
    void givenIncorrectCatIdToUpdate_whenUpdate_thenExceptionIsThrown() {
        NewCategoryDto newCategoryDto = DataUtils.getNewCategoryDto();

        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceTest.update(newCategoryDto, 1L)
        );
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void givenTwoCategories_whenGetAll_thenTwoCategoriesAreReturned() {
        Category category1 = DataUtils.getCategoryPersisted();
        Category category2 = DataUtils.getCategoryPersisted();
        category2.setId(2L);
        category2.setName("Cat2");
        List<Category> categories = List.of(category1, category2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        BDDMockito.given(categoryRepository.findAll(any(Pageable.class))).willReturn(categoryPage);

        CategoryDto categoryDto1 = DataUtils.getCategoryDto();
        CategoryDto categoryDto2 = DataUtils.getCategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Cat2");

        BDDMockito.given(categoryDtoMapper.categoryToCategoryDto(category1)).willReturn(categoryDto1);
        BDDMockito.given(categoryDtoMapper.categoryToCategoryDto(category2)).willReturn(categoryDto2);

        List<CategoryDto> result = categoryServiceTest.getAll(PageRequest.of(0, 2));
        assertEquals(2, result.size());
        assertEquals("Category name", result.get(0).getName());
        assertEquals("Cat2", result.get(1).getName());
    }

    @Test
    @DisplayName("Test get by id functionality")
    void givenId_whenGetById_thenCategoryIsReturned() {
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.of(DataUtils.getCategoryPersisted()));
        BDDMockito.given(categoryDtoMapper.categoryToCategoryDto(any(Category.class))).willReturn(DataUtils.getCategoryDto());
        CategoryDto obtainedCategory = categoryServiceTest.getById(1L);
        assertThat(obtainedCategory).isNotNull();
    }

    @Test
    @DisplayName("Test get by incorrect id functionality")
    void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        BDDMockito.given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> categoryServiceTest.getById(1L)
        );

        verify(categoryDtoMapper, never()).categoryToCategoryDto(any(Category.class));
    }
}
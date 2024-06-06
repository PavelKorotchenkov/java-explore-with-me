package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.CategoryInUseException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryDtoMapper categoryDtoMapper;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category saved = categoryRepository.save(categoryDtoMapper.newCategoryDtoToCategory(newCategoryDto));
        return categoryDtoMapper.categoryToCategoryDto(saved);
    }

    @Override
    public void delete(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found"));
        Optional<Event> optEvent = eventRepository.findFirstByCategoryId(catId);
        if (optEvent.isPresent()) {
            throw new CategoryInUseException("Failed to delete category with id='" + catId + "'");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto update(NewCategoryDto newCategoryDto, long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found"));
        category.setName(newCategoryDto.getName());
        Category saved = categoryRepository.save(category);
        return categoryDtoMapper.categoryToCategoryDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAll(PageRequest pageable) {
        return categoryRepository
                .findAll(pageable)
                .map(categoryDtoMapper::categoryToCategoryDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found"));

        return categoryDtoMapper.categoryToCategoryDto(category);
    }
}

package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryDtoMapper;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.CategoryInUseException;
import ru.practicum.exception.CategoryUniqueNameViolationException;
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
	public CategoryDto addNew(NewCategoryDto newCategoryDto) {
		Optional<Category> existingCategory = categoryRepository.findByName(newCategoryDto.getName());
		if (existingCategory.isPresent()) {
			throw new CategoryUniqueNameViolationException("Attempt to create category with name='" + newCategoryDto.getName() + "' failed");
		}

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

		categoryRepository.findByName(newCategoryDto.getName())
				.filter(existingCategory -> existingCategory.getId() != catId)
				.ifPresent(existingCategory -> {
					throw new CategoryUniqueNameViolationException("Failed to change category name to '" + newCategoryDto.getName() + "'");
				});

		category.setName(newCategoryDto.getName());
		Category saved = categoryRepository.save(category);
		return categoryDtoMapper.categoryToCategoryDto(saved);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CategoryDto> getAll(Pageable pageable) {
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
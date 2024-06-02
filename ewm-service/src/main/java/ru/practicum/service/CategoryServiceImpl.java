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

	@Transactional
	@Override
	public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
		Optional<Category> existingCategory = categoryRepository.findByName(newCategoryDto.getName());
		if (existingCategory.isPresent()) {
			throw new CategoryUniqueNameViolationException("Attempt to create category with name='" + newCategoryDto.getName() + "' failed");
		}

		Category saved = categoryRepository.save(CategoryDtoMapper.INSTANCE.newCategoryDtoToCategory(newCategoryDto));
		return CategoryDtoMapper.INSTANCE.categoryToCategoryDto(saved);
	}

	@Override
	public void deleteCategory(long catId) {
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
	public CategoryDto updateCategory(NewCategoryDto newCategoryDto, long catId) {
		Category category = categoryRepository.findById(catId)
				.orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found"));

		Optional<Category> existingCategoryWithTheSameName = categoryRepository.findByName(newCategoryDto.getName());
		if (existingCategoryWithTheSameName.isPresent() && existingCategoryWithTheSameName.get().getId() != catId) {
			throw new CategoryUniqueNameViolationException("Failed to change category name to '" + newCategoryDto.getName() + "'");
		}

		category.setName(newCategoryDto.getName());
		Category saved = categoryRepository.save(category);
		return CategoryDtoMapper.INSTANCE.categoryToCategoryDto(saved);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CategoryDto> getCategories(Pageable pageable) {
		return categoryRepository
				.findAll(pageable)
				.map(CategoryDtoMapper.INSTANCE::categoryToCategoryDto)
				.getContent();
	}


	@Transactional(readOnly = true)
	@Override
	public CategoryDto getCategoryById(long catId) {
		Category category = categoryRepository.findById(catId)
				.orElseThrow(() -> new EntityNotFoundException("Category with id='" + catId + "' not found"));

		return CategoryDtoMapper.INSTANCE.categoryToCategoryDto(category);
	}
}

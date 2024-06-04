package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
	CategoryDto addNew(NewCategoryDto newCategoryDto);

	void delete(long catId);

	CategoryDto update(NewCategoryDto newCategoryDto, long catId);

	List<CategoryDto> getAll(Pageable pageable);

	CategoryDto getById(long catId);
}
